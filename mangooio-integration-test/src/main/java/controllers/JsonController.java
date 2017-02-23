package controllers;

import io.mangoo.annotations.Routing;
import io.mangoo.routing.Response;
import io.mangoo.routing.bindings.Request;
import models.Person;

public class JsonController {
    private static final int AGE = 24;

    @Routing(method = "GET", url = "/render")
    public Response render() {
        Person person = new Person("Peter", "Parker", AGE);
        return Response.withOk().andJsonBody(person);
    }

    @Routing(method = "POST", url = "/parse")
    public Response parse(Person person) {
        return Response.withOk().andContent("person", person);
    }

    @Routing(method = "POST", url = "/body")
    public Response body(Request request) {
        return Response.withOk().andTextBody(request.getURI());
    }

    @Routing(method = "POST", url = "/requestAndJson")
    public Response requestAndJson(Request request, Person person) {
        return Response.withOk().andTextBody(request.getURI() + person.getFirstname());
    }
}