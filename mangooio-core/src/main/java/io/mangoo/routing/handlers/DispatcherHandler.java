package io.mangoo.routing.handlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.mangoo.annotations.FilterWith;
import io.mangoo.configuration.Config;
import io.mangoo.core.Application;
import io.mangoo.crypto.Crypto;
import io.mangoo.i18n.Messages;
import io.mangoo.interfaces.MangooRequestFilter;
import io.mangoo.models.Identity;
import io.mangoo.routing.Attachment;
import io.mangoo.routing.listeners.MetricsListener;
import io.mangoo.templating.TemplateEngine;
import io.mangoo.utils.RequestUtils;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * Main class for dispatching a request to the request chain.
 * The request chain contains the following handlers in order:
 *
 * DispatcherHandler
 * LocalHandler
 * InboundCookiesHandler
 * FormHandler
 * RequestHandler
 * OutboundCookiesHandler
 * ResponseHandler
 *
 * @author svenkubiak
 *
 */
public class DispatcherHandler implements HttpHandler {
    private static final Config CONFIG = Application.getConfig();
    private static final Logger LOG = LogManager.getLogger(DispatcherHandler.class);
    private Method method;
    private List<Annotation> methodAnnotations = new ArrayList<>();
    private List<Annotation> classAnnotations = new ArrayList<>();
    private final TemplateEngine templateEngine;
    private final Messages messages;
    private final Crypto crypto;
    private final Map<String, Class<?>> methodParameters;
    private final Class<?> controllerClass;
    private final String controllerClassName;
    private final String controllerMethodName;
    private final String username;
    private final String password;    
    private final int methodParametersCount;
    private final boolean async;
    private final boolean timer;
    private final boolean hasRequestFilter;

    public DispatcherHandler(Class<?> controllerClass, String controllerMethod, boolean async, boolean internalTemplateEngine, boolean timer, String username, String password) {
        Objects.requireNonNull(controllerClass, "controllerClass can not be null");
        Objects.requireNonNull(controllerMethod, "controllerMethod can not be null");

        this.templateEngine = internalTemplateEngine ? Application.getInternalTemplateEngine() : Application.getInstance(TemplateEngine.class);
        this.messages = Application.getInstance(Messages.class);
        this.crypto = Application.getInstance(Crypto.class);
        this.controllerClass = controllerClass;
        this.controllerMethodName = controllerMethod;
        this.controllerClassName = controllerClass.getSimpleName();
        this.username = username;
        this.password = password;
        this.methodParameters = getMethodParameters();
        this.methodParametersCount = this.methodParameters.size();
        this.async = async;
        this.timer = timer;
        this.hasRequestFilter = Application.getInjector().getAllBindings().containsKey(com.google.inject.Key.get(MangooRequestFilter.class));

        try {
            this.method = Application.getInstance(this.controllerClass)
                    .getClass()
                    .getMethod(this.controllerMethodName, this.methodParameters.values().toArray(new Class[0]));
            
            for (Annotation annotation : this.method.getAnnotations()) {
                if (annotation.annotationType().equals(FilterWith.class)) {
                    this.methodAnnotations.add(annotation);
                }
            }
        } catch (NoSuchMethodException | SecurityException e) {
            LOG.error("Failed to create DispatcherHandler", e);
        }
        
        for (Annotation annotation : controllerClass.getAnnotations()) {
            if (annotation.annotationType().equals(FilterWith.class)) {
                this.classAnnotations.add(annotation);
            }
        }
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if ( (RequestUtils.isPostOrPut(exchange) || this.async) && exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        if (CONFIG.isAdminEnabled()) {
            exchange.addExchangeCompleteListener(new MetricsListener(System.currentTimeMillis()));
        }

        final Attachment attachment = Attachment.build()
            .withControllerInstance(Application.getInstance(this.controllerClass))
            .withControllerClass(this.controllerClass)
            .withControllerClassName(this.controllerClassName)
            .withControllerMethodName(this.controllerMethodName)
            .withClassAnnotations(this.classAnnotations)
            .withMethodAnnotations(this.methodAnnotations)
            .withMethodParameters(this.methodParameters)
            .withMethod(this.method)
            .withMethodParameterCount(this.methodParametersCount)
            .withRequestFilter(this.hasRequestFilter)
            .withRequestParameter(RequestUtils.getRequestParameters(exchange))
            .withMessages(this.messages)
            .withTimer(this.timer)
            .withTemplateEngine(this.templateEngine)
            .withCrypto(this.crypto);

        exchange.putAttachment(RequestUtils.ATTACHMENT_KEY, attachment);
        nextHandler(exchange);
    }

    /**
     * Converts the method parameter of a mapped controller method to a map
     *
     * @return A Map containing the declared methods of the method parameters and their class type
     */
    private Map<String, Class<?>> getMethodParameters() {
        final Map<String, Class<?>> parameters = new LinkedHashMap<>(); //NOSONAR
        for (final Method declaredMethod : this.controllerClass.getDeclaredMethods()) {
            if (declaredMethod.getName().equals(this.controllerMethodName) && declaredMethod.getParameterCount() > 0) {
                Arrays.asList(declaredMethod.getParameters()).forEach(parameter -> parameters.put(parameter.getName(), parameter.getType())); //NOSONAR
                break;
            }
        }

        return parameters;
    }

    /**
     * Handles the next request in the handler chain
     *
     * @param exchange The HttpServerExchange
     * @throws Exception Thrown when an exception occurs
     */
    @SuppressWarnings("all")
    private void nextHandler(HttpServerExchange exchange) throws Exception {
        if (requestHasAuthentication()) {
            HttpHandler httpHandler = addSecurity(Application.getInstance(AuthenticationHandler.class));
            httpHandler.handleRequest(exchange);
        } else {
            Application.getInstance(LocaleHandler.class).handleRequest(exchange);
        }
    }
    
    /**
     * Checks if the request requires basic authentication
     * 
     * @return True if username and password are not blank, false otherwise
     */
    private boolean requestHasAuthentication() {
        return StringUtils.isNotBlank(this.username) && StringUtils.isNotBlank(this.password);
    }

    /**
     * Adds a Wrapper to the handler when the request requires authentication
     * 
     * @param wrap The Handler to wrap
     * @return A wrapped handler
     */
    private HttpHandler addSecurity(final HttpHandler wrap) {
        HttpHandler handler = wrap;
        
        final List<AuthenticationMechanism> mechanisms = Collections.<AuthenticationMechanism>singletonList(new BasicAuthenticationMechanism("Authentication required"));
        handler = new AuthenticationCallHandler(handler);
        handler = new AuthenticationConstraintHandler(handler);
        handler = new AuthenticationMechanismsHandler(handler, mechanisms);
        handler = new SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE, new Identity(this.username, this.password), handler);
        
        return handler;
    }
}