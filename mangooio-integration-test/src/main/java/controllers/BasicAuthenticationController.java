package controllers;

import io.mangoo.annotations.BasicAuthentication;
import io.mangoo.annotations.Routing;
import io.mangoo.routing.Response;

/**
 * 
 * @author svenkubiak
 *
 */

public class BasicAuthenticationController {
    
    @BasicAuthentication(username = "credentials.username", password = "credentials.password")
    @Routing(method = "GET", url = "/basicauth")
    public Response basicauth() {
        return Response.withOk().andTextBody("authenticated");
    }
}