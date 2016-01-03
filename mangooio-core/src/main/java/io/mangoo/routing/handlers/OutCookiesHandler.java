package io.mangoo.routing.handlers;

import java.time.LocalDateTime;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Joiner;

import io.mangoo.authentication.Authentication;
import io.mangoo.enums.Default;
import io.mangoo.routing.RequestAttachment;
import io.mangoo.routing.bindings.Flash;
import io.mangoo.routing.bindings.Session;
import io.mangoo.utils.CookieBuilder;
import io.mangoo.utils.RequestUtils;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;

/**
 *
 * @author svenkubiak
 *
 */
public class OutCookiesHandler implements HttpHandler {
    private RequestAttachment requestAttachment;

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        this.requestAttachment = exchange.getAttachment(RequestUtils.REQUEST_ATTACHMENT);

        setSessionCookie(exchange, requestAttachment.getSession());
        setFlashCookie(exchange, requestAttachment.getFlash());
        setAuthenticationCookie(exchange, requestAttachment.getAuthentication());

        nextHandler(exchange);
    }

    /**
     * Sets the session cookie to the current HttpServerExchange
     *
     * @param exchange The Undertow HttpServerExchange
     */
    private void setSessionCookie(HttpServerExchange exchange, Session session) {
        if (session != null && session.hasChanges()) {
            final String data = Joiner.on(Default.SPLITTER.toString()).withKeyValueSeparator(Default.SEPERATOR.toString()).join(session.getValues());
            final String version = this.requestAttachment.getConfig().getCookieVersion();
            final String authenticityToken = session.getAuthenticityToken();
            final LocalDateTime expires = session.getExpires();
            final StringBuilder buffer = new StringBuilder()
                    .append(DigestUtils.sha512Hex(data + authenticityToken + expires + version + this.requestAttachment.getConfig().getApplicationSecret()))
                    .append(Default.DELIMITER.toString())
                    .append(authenticityToken)
                    .append(Default.DELIMITER.toString())
                    .append(expires)
                    .append(Default.DELIMITER.toString())
                    .append(version)
                    .append(Default.DATA_DELIMITER.toString())
                    .append(data);

            String value = buffer.toString();
            if (this.requestAttachment.getConfig().isSessionCookieEncrypt()) {
                value = this.requestAttachment.getCrypto().encrypt(value);
            }

            final Cookie cookie = CookieBuilder.create()
                .name(this.requestAttachment.getConfig().getSessionCookieName())
                .value(value)
                .secure(this.requestAttachment.getConfig().isSessionCookieSecure())
                .httpOnly(true)
                .expires(expires)
                .build();

            exchange.setResponseCookie(cookie);
        }
    }

    /**
     * Sets the authentication cookie to the current HttpServerExchange
     *
     * @param exchange The Undertow HttpServerExchange
     */
    private void setAuthenticationCookie(HttpServerExchange exchange, Authentication authentication) {
        if (authentication != null && authentication.hasAuthenticatedUser()) {
            Cookie cookie;
            final String cookieName = this.requestAttachment.getConfig().getAuthenticationCookieName();
            if (authentication.isLogout()) {
                cookie = exchange.getRequestCookies().get(cookieName);
                cookie.setSecure(this.requestAttachment.getConfig().isAuthenticationCookieSecure());
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                cookie.setDiscard(true);
            } else {
                final String authenticatedUser = authentication.getAuthenticatedUser();
                final LocalDateTime expires = authentication.isRemember() ? LocalDateTime.now().plusSeconds(this.requestAttachment.getConfig().getAuthenticationRememberExpires()) : authentication.getExpires();
                final String version = this.requestAttachment.getConfig().getAuthCookieVersion();

                final StringBuilder buffer = new StringBuilder()
                        .append(DigestUtils.sha512Hex(authenticatedUser + expires + version + this.requestAttachment.getConfig().getApplicationSecret()))
                        .append(Default.DELIMITER.toString())
                        .append(expires)
                        .append(Default.DELIMITER.toString())
                        .append(version)
                        .append(Default.DATA_DELIMITER.toString())
                        .append(authenticatedUser);

                String value = buffer.toString();
                if (this.requestAttachment.getConfig().isAuthenticationCookieEncrypt()) {
                    value = this.requestAttachment.getCrypto().encrypt(value);
                }

                cookie = CookieBuilder.create()
                        .name(cookieName)
                        .value(value)
                        .secure(this.requestAttachment.getConfig().isAuthenticationCookieSecure())
                        .httpOnly(true)
                        .expires(expires)
                        .build();
            }

            exchange.setResponseCookie(cookie);
        }
    }

    /**
     * Sets the flash cookie to current HttpServerExchange
     *
     * @param exchange The Undertow HttpServerExchange
     */
    private void setFlashCookie(HttpServerExchange exchange, Flash flash) {
        if (flash != null && !flash.isDiscard() && flash.hasContent()) {
            final String values = Joiner.on("&").withKeyValueSeparator(":").join(flash.getValues());

            final Cookie cookie = CookieBuilder.create()
                    .name(this.requestAttachment.getConfig().getFlashCookieName())
                    .value(values)
                    .secure(this.requestAttachment.getConfig().isFlashCookieSecure())
                    .httpOnly(true)
                    .build();

            exchange.setResponseCookie(cookie);
        } else {
            final Cookie cookie = exchange.getRequestCookies().get(this.requestAttachment.getConfig().getFlashCookieName());
            if (cookie != null) {
                cookie.setHttpOnly(true)
                .setSecure(this.requestAttachment.getConfig().isFlashCookieSecure())
                .setPath("/")
                .setMaxAge(0);

                exchange.setResponseCookie(cookie);
            }
        }
    }

    /**
     * Handles the next request in the handler chain
     *
     * @param exchange The HttpServerExchange
     * @throws Exception Thrown when an exception occurs
     */
    @SuppressWarnings("all")
    private void nextHandler(HttpServerExchange exchange) throws Exception {
        new ResponseHandler().handleRequest(exchange);
    }
}