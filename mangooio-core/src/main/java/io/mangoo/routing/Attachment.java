package io.mangoo.routing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import io.mangoo.crypto.Crypto;
import io.mangoo.enums.Required;
import io.mangoo.i18n.Messages;
import io.mangoo.models.Subject;
import io.mangoo.routing.bindings.Authentication;
import io.mangoo.routing.bindings.Flash;
import io.mangoo.routing.bindings.Form;
import io.mangoo.routing.bindings.Request;
import io.mangoo.routing.bindings.Session;
import io.mangoo.templating.TemplateEngine;

/**
 *
 * @author svenkubiak
 *
 */
public class Attachment {
    private final long start = System.currentTimeMillis();
    private int methodParametersCount;
    private int limit;
    private Class<?> controllerClass;
    private Object controllerInstance;
    private Map<String, Class<?>> methodParameters;
    private String controllerClassName;
    private String controllerMethodName;
    private String body;
    private String username;
    private String password;
    private Method method;
    private Authentication authentication;
    private Session session;
    private Subject subject;
    private Flash flash;
    private Form form;
    private Request request;
    private Response response;
    private Map<String, String> requestParameter;
    private Crypto crypto;
    private Messages messages;
    private TemplateEngine templateEngine;
    private boolean requestFilter;
    private boolean timer;
    private List<Annotation> classAnnotations;
    private List<Annotation> methodAnnotations;
    private Locale locale;

    public static Attachment build() {
        return new Attachment();
    }

    public Attachment withControllerClass(Class<?> controllerClass) {
        this.controllerClass = Objects.requireNonNull(controllerClass, Required.CONTROLLER_CLASS.toString());
        return this;
    }

    public Attachment withControllerClassName(String controllerClassName) {
        this.controllerClassName = Objects.requireNonNull(controllerClassName, Required.CONTROLLER_NAME.toString());
        return this;
    }

    public Attachment withControllerMethodName(String controllerMethodName) {
        this.controllerMethodName = Objects.requireNonNull(controllerMethodName, Required.CONTROLLER_METHOD.toString());
        return this;
    }

    public Attachment withControllerInstance(Object controllerInstance) {
        this.controllerInstance = Objects.requireNonNull(controllerInstance, Required.CONTROLLER_INSTANCE.toString());
        return this;
    }

    public Attachment withCrypto(Crypto crypto) {
        this.crypto = Objects.requireNonNull(crypto, Required.CRYPTO.toString());
        return this;
    }

    public Attachment withMethodParameters(Map<String, Class<?>> methodParameters) {
        this.methodParameters = Objects.requireNonNull(methodParameters, Required.METHOD_PARAMETERS.toString());
        return this;
    }

    public Attachment withMessages(Messages messages) {
        this.messages = Objects.requireNonNull(messages, Required.MESSAGES.toString());
        return this;
    }

    public Attachment withTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = Objects.requireNonNull(templateEngine, Required.TEMPLATE_ENGINE.toString());
        return this;
    }

    public Attachment withRequestParameter(Map<String, String> requestParameter) {
        this.requestParameter = Objects.requireNonNull(requestParameter, Required.REQUEST_PARAMETER.toString());
        return this;
    }

    public Attachment withMethod(Method method) {
        this.method = Objects.requireNonNull(method, Required.METHOD.toString());
        return this;
    }
    
    public Attachment withLocale(Locale locale) {
        this.locale = Objects.requireNonNull(locale, Required.LOCALE.toString());
        return this;
    }

    public Attachment withMethodParameterCount(int methodParametersCount) {
        this.methodParametersCount = methodParametersCount;
        return this;
    }

    public Attachment withRequestFilter(boolean hasRequestFilter) {
        this.requestFilter = hasRequestFilter;
        return this;
    }
    
    public Attachment withClassAnnotations(List<Annotation> classAnnotations) {
        this.classAnnotations = new ArrayList<>(classAnnotations);
        return this;
    }
    
    public Attachment withMethodAnnotations(List<Annotation> methodAnnotations) {
        this.methodAnnotations = new ArrayList<>(methodAnnotations);
        return this;
    }
    
    public Attachment withTimer(boolean timer) {
        this.timer = timer;
        return this;
    }

    public Attachment withLimit(int limit) {
        this.limit = limit;
        return this;
    }
    
    public Attachment withUsername(String username) {
        this.username = username;
        return this;
    }
    
    public Attachment withPassword(String password) {
        this.password = password;
        return this;
    }

    public Messages getMessages() {
        return this.messages;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setFlash(Flash flash) {
        this.flash = flash;
    }
    
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public Session getSession() {
        return this.session;
    }

    public Flash getFlash() {
        return this.flash;
    }

    public Authentication getAuthentication() {
        return this.authentication;
    }

    public Crypto getCrypto() {
        return this.crypto;
    }

    public boolean hasRequestFilter() {
        return this.requestFilter;
    }

    public Method getMethod() {
        return this.method;
    }

    public Class<?> getControllerClass() {
        return this.controllerClass;
    }

    public Request getRequest() {
        return this.request;
    }

    public Map<String, Class<?>> getMethodParameters() {
        return this.methodParameters;
    }

    public Object getControllerInstance() {
        return this.controllerInstance;
    }

    public TemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    public Form getForm() {
        return this.form;
    }

    public String getControllerClassName() {
        return this.controllerClassName;
    }

    public String getControllerMethodName() {
        return this.controllerMethodName;
    }

    public int getMethodParametersCount() {
        return this.methodParametersCount;
    }

    public  Map<String, String> getRequestParameter() {
        return this.requestParameter;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return this.response;
    }
    
    public boolean hasTimer() {
        return this.timer;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public boolean hasAuthentication() {
        return StringUtils.isNotBlank(this.username) && StringUtils.isNotBlank(this.password);
    }
    
    public boolean hasLimit() {
        return this.limit > 0;
    }

    public long getResponseTime() {
        return System.currentTimeMillis() - this.start;
    }
    
    public Locale getLocale() {
        return this.locale;
    }

    public List<Annotation> getClassAnnotations() {
        return this.classAnnotations;
    }
    
    public List<Annotation> getMethodAnnotations() {
        return this.methodAnnotations;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public String getControllerAndMethod() {
        return this.controllerClassName + ":" + this.controllerMethodName;
    }
}