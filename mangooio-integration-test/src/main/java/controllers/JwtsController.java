package controllers;

import io.mangoo.annotations.FilterWith;
import io.mangoo.annotations.Routing;
import io.mangoo.filters.JsonWebTokenFilter;
import io.mangoo.routing.Response;
import io.mangoo.routing.bindings.Request;

/**
 * 
 * @author sven.kubiak
 *
 */
public class JwtsController {
    @FilterWith(JsonWebTokenFilter.class)
    @Routing(method = "GET", url = "/jwts/validate")
    public Response validate(Request request) {
        return Response.withOk().andEmptyBody();
    }
    
    @FilterWith(JsonWebTokenFilter.class)
    @Routing(method = "GET", url = "/jwts/retrieve")
    public Response retrieve(Request request) {
        String valid = String.valueOf(request.getJsonWebToken().isPresent());
        return Response.withOk().andTextBody(valid);
    }
}