package controllers;

import io.mangoo.annotations.Routing;
import io.mangoo.routing.Response;
import io.mangoo.routing.bindings.Session;

public class SessionController {
    
    @Routing(method = "GET", url = "/session")
    public Response session(Session session) {
        session.put("foo", "this is a session value");

        return Response.withOk().andEmptyBody();
    }
}
