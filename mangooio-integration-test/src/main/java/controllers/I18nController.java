package controllers;

import io.mangoo.annotations.Routing;
import io.mangoo.enums.Default;
import io.mangoo.routing.Response;
import io.mangoo.utils.cookie.CookieBuilder;
import io.undertow.server.handlers.Cookie;

public class I18nController {
    
    @Routing(method = "GET", url = "/translation")
    public Response translation() {
        return Response.withOk();
    }
    
    @Routing(method = "GET", url = "/localize")
    public Response localize() {
        Cookie cookie = CookieBuilder.create()
                .name(Default.COOKIE_I18N_NAME.toString())
                .value("en")
                .build();
        
        return Response.withOk().andCookie(cookie);
    }
    
    @Routing(method = "GET", url = "/special")
    public Response special() {
        return Response.withOk();
    }
    
    @Routing(method = "GET", url = "/umlaute")
    public Response umlaute() {
        return Response.withOk();
    }
}