package io.mangoo.routing.handlers;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import io.mangoo.configuration.Config;
import io.mangoo.core.Application;
import io.mangoo.enums.ContentType;
import io.mangoo.enums.Header;
import io.mangoo.enums.Required;
import io.mangoo.enums.Template;
import io.mangoo.templating.TemplateEngine;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

/**
 *
 * @author svenkubiak
 *
 */
public class ExceptionHandler implements HttpHandler {
    private static final Logger LOG = LogManager.getLogger(ExceptionHandler.class);
    private Config config;
    
    @Inject
    public ExceptionHandler(Config config) {
        this.config = Objects.requireNonNull(config, Required.CONFIG.toString());
    }
    
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Throwable throwable = exchange.getAttachment(io.undertow.server.handlers.ExceptionHandler.THROWABLE);
        if (throwable != null) {
            LOG.error("Internal server exception", throwable);
        }
        
        try {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ContentType.TEXT_HTML.toString());
            exchange.getResponseHeaders().put(Header.X_XSS_PPROTECTION.toHttpString(), this.config.getXssProectionHeader());
            exchange.getResponseHeaders().put(Header.X_CONTENT_TYPE_OPTIONS.toHttpString(), this.config.getXContentTypeOptionsHeader());
            exchange.getResponseHeaders().put(Header.X_FRAME_OPTIONS.toHttpString(), this.config.getXFrameOptionsHeader());
            exchange.getResponseHeaders().put(Header.CONTENT_SECURITY_POLICY.toHttpString(), this.config.getContentSecurityPolicyHeader());
            exchange.getResponseHeaders().put(Headers.SERVER, this.config.getServerHeader());
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);

            if (Application.inDevMode()) {
                TemplateEngine templateEngine = Application.getInternalTemplateEngine();
                if (throwable == null) {
                    exchange.getResponseSender().send(Template.DEFAULT.serverError());
                } else if (throwable.getCause() == null) {
                    exchange.getResponseSender().send(templateEngine.renderException(exchange, throwable, true));
                } else {
                    exchange.getResponseSender().send(templateEngine.renderException(exchange, throwable.getCause(), false));
                }
            } else {
                exchange.getResponseSender().send(Template.DEFAULT.serverError());
            }
        } catch (Exception e) { //NOSONAR
            LOG.error("Failed to pass an exception to the frontend", e);
        }
    }
}