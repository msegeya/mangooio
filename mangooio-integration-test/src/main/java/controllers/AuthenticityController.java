package controllers;

import io.mangoo.annotations.FilterWith;
import io.mangoo.annotations.Routing;
import io.mangoo.filters.AuthenticityFilter;
import io.mangoo.routing.Response;

public class AuthenticityController {
    
    @Routing(method = "GET", url = "/authenticityform")
    public Response form() {
        return Response.withOk().andContent("foo", "bar");
    }
    
    @Routing(method = "GET", url = "/authenticitytoken")
    public Response token() {
        return Response.withOk().andContent("foo", "bar");
    }
    
    @FilterWith(AuthenticityFilter.class)
    @Routing(method = "GET", url = "/valid")
    public Response valid() {
        return Response.withOk().andContent("foo", "bar");
    }
    
    @FilterWith(AuthenticityFilter.class)
    @Routing(method = "GET", url = "/invalid")
    public Response invalid() {
        return Response.withOk().andContent("foo", "bar");
    }
}