package controllers;

import io.mangoo.annotations.Routing;
import io.mangoo.routing.Response;
import io.mangoo.routing.bindings.Flash;

public class FlashController {
    private static final String SUCCESS = "success";
    private static final String WARNING = "warning";
    private static final String ERROR = "error";
    private static final String SIMPLE = "simple";

    @Routing(method = "GET", url = "/flash")
    public Response flash(Flash flash) {
        flash.put(SIMPLE, SIMPLE);
        flash.setError(ERROR);
        flash.setWarning(WARNING);
        flash.setSuccess(SUCCESS);

        return Response.withRedirect("/flashed");
    }

    @Routing(method = "GET", url = "/flashed")
    public Response flashed() {
        return Response.withOk();
    }
}