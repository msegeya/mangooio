package controllers;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import io.mangoo.annotations.Routing;
import io.mangoo.routing.Response;
import io.mangoo.routing.bindings.Request;
import io.undertow.util.HttpString;

public class ApplicationController {

    @Routing(method = "GET", url = "/", blocking = true, timer = true)
    public Response index() {
        return Response.withOk();
    }

    public Response route() {
        return Response.withOk();
    }

    @Routing(method = "GET", url = "/redirect")
    public Response redirect() {
        return Response.withRedirect("/");
    }

    public Response restricted() {
        return Response.withOk().andContent("form", "foo");
    }

    @Routing(method = "GET", url = "/text")
    public Response text() {
        return Response.withOk().andTextBody("foo");
    }

    @Routing(method = "GET", url = "/limit", limit = 10)
    public Response limit() {
        return Response.withOk().andEmptyBody();
    }


    public Response reverse() {
        return Response.withOk();
    }
    
    @Routing(method = "GET", url = "/prettytime")
    public Response prettytime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDate localDate = LocalDate.now();
        Date date = new Date();

        return Response.withOk()
                .andContent("localDateTime", localDateTime)
                .andContent("localDate", localDate)
                .andContent("date", date);
    }

    @Routing(method = "GET", url = "/forbidden")
    public Response forbidden() {
        return Response.withForbidden().andEmptyBody();
    }

    @Routing(method = "GET", url = "/badrequest")
    public Response badrequest() {
        return Response.withBadRequest().andEmptyBody();
    }

    @Routing(method = "GET", url = "/unauthorized")
    public Response unauthorized() {
        return Response.withUnauthorized().andEmptyBody();
    }

    @Routing(method = "GET", url = "/etag")
    public Response etag() {
        return Response.withOk().andTextBody("foo").andEtag();
    }

    @SuppressWarnings("all")
    @Routing(method = "GET", url = "/binary")
    public Response binary() {
        final URL url = this.getClass().getResource("/attachment.txt");
        final File file = new File(url.getFile());

        return Response.withOk().andBinaryFile(file);
    }

    @Routing(method = "GET", url = "/request")
    public Response request(Request request) {
        return Response.withOk().andTextBody(request.getURI());
    }

    @Routing(method = "POST", url = "/post")
    public Response post(Request request) {
        return Response.withOk().andTextBody(request.getBody());
    }

    @Routing(method = "PUT", url = "put")
    public Response put(Request request) {
        return Response.withOk().andTextBody(request.getBody());
    }

    @Routing(method = "POST", url = "jsonpathpost")
    public Response jsonPathPost(Request request) {
        return Response.withOk().andTextBody(request.getBodyAsJsonPath().jsonString());
    }

    @Routing(method = "PUT", url = "jsonpathput")
    public Response jsonPathPut(Request request) {
        return Response.withOk().andTextBody(request.getBodyAsJsonPath().jsonString());
    }

    @Routing(method = "POST", url = "jsonboonpost")
    public Response jsonBoonPost(Request request) {
        return Response.withOk().andTextBody(request.getBodyAsJsonMap().toString());
    }

    @Routing(method = "PUT", url = "jsonboonput")
    public Response jsonBoonPut(Request request) {
        return Response.withOk().andTextBody(request.getBodyAsJsonMap().toString());
    }

    @Routing(method = "GET", url = {"/location", "/location/{myloca}"})
    public Response location(String myloc) {
        return Response.withOk().andContent("myloc", myloc);
    }

    @Routing(method = "GET", url = "/header", blocking = true)
    public Response header() {
        return Response
                .withOk()
                .andEmptyBody()
                .andHeader(new HttpString("Access-Control-Allow-Origin"), "https://mangoo.io");
    }
}
