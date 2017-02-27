package controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.mangoo.annotations.Routing;
import io.mangoo.routing.Response;

public class ParameterController {
    private static final String FOO = "foo";
    private static final String PARAM_TEMPLATE = "/ParameterController/param.ftl";
    private static final String MULTIPARAM_TEMPLATE = "/ParameterController/multiparam.ftl";

    @Routing(method = "GET", url = {"/string/{foo}", "/string"})
    public Response stringParam(String foo) {
        if (foo == null) {foo = "isNull";}
        return Response.withOk().andTemplate(PARAM_TEMPLATE).andContent(FOO, foo);
    }
    
    @Routing(method = "GET", url = "/double/{foo}")
    public Response doubleParam(Double foo) {
        return Response.withOk().andTextBody(String.valueOf(foo));
    }

    @Routing(method = "GET", url = "/doublePrimitive/{foo}")
    public Response doublePrimitiveParam(double foo) {
        return Response.withOk().andTextBody(String.valueOf(foo));
    }

    @Routing(method = "GET", url = "/int/{foo}")
    public Response intParam(int foo) {
        return Response.withOk().andTemplate(PARAM_TEMPLATE).andContent(FOO, foo);
    }

    @Routing(method = "GET", url = "/integer/{foo}")
    public Response integerParam(Integer foo) {
        return Response.withOk().andTemplate(PARAM_TEMPLATE).andContent(FOO, foo);
    }

    @Routing(method = "GET", url = "/float/{foo}")
    public Response floatParam(Float foo) {
        return Response.withOk().andTextBody(String.valueOf(foo));
    }

    @Routing(method = "GET", url = "/floatPrimitive/{foo}")
    public Response floatPrimitiveParam(float foo) {
        return Response.withOk().andTextBody(String.valueOf(foo));
    }

    @Routing(method = "GET", url = "/long/{foo}")
    public Response longParam(Long foo) {
        return Response.withOk().andTextBody(String.valueOf(foo));
    }

    @Routing(method = "GET", url = "/longPrimitive/{foo}")
    public Response longPrimitiveParam(long foo) {
        return Response.withOk().andTextBody(String.valueOf(foo));
    }

    @Routing(method = "GET", url = "/multiple/{foo}/{bar}")
    public Response multipleParam(String foo, int bar) {
        return Response.withOk().andTemplate(MULTIPARAM_TEMPLATE).andContent(FOO, foo).andContent("bar", bar);
    }

    @Routing(method = "GET", url = "/path")
    public Response pathParam(String foo) {
        return Response.withOk().andTemplate(PARAM_TEMPLATE).andContent(FOO, foo);
    }

    @Routing(method = "GET", url = "/localdate/{localDate}")
    public Response localdate(LocalDate localDate) {
        return Response
                .withOk()
                .andTextBody(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    @Routing(method = "GET", url = "/localdatetime/{localDateTime}")
    public Response localdatetime(LocalDateTime localDateTime) {
        return Response
                .withOk()
                .andTextBody(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}