package io.mangoo.routing;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Preconditions;

import io.mangoo.enums.Required;
import io.mangoo.enums.RouteType;
import io.mangoo.interfaces.MangooWebSocket;

/**
 *
 * @author svenkubiak
 *
 */
public final class Router {
    private static Set<Route> routes = ConcurrentHashMap.newKeySet();
    private static final int MAX_ROUTES = 100000;

    private Router(){
    }

    /**
     * Adds a new route to the router
     *
     * @param route The route to add
     */
    public static void addRoute(Route route) {
        Objects.requireNonNull(route, Required.ROUTE.toString());
        Preconditions.checkArgument(routes.size() <= MAX_ROUTES, "Maximum of " + MAX_ROUTES + " routes reached");
        
        routes.add(route);
    }

    /**
     * @return An unmodifiable set of all configured routes
     */
    public static Set<Route> getRoutes() {
        return Collections.unmodifiableSet(routes);
    }

    /**
     * Adds a new Server Sent Event route to the router
     * 
     * @param url The URL of the route
     * @param requireAuthentucation True if login is required to access route, false otherwise
     */
    public static void addServerSentEventRoute(String url, boolean requireAuthentucation) {
        Objects.requireNonNull(url, Required.URL.toString());
        
        Route route = new Route(RouteType.SERVER_SENT_EVENT).toUrl(url).withAuthentication(requireAuthentucation);
        Router.addRoute(route);
    }

    /**
     * Adds a new WebSocket route to the router
     * @param url The URL of the route
     * @param controllerClass The controller class
     * @param requireAuthentucation True if login is required to access route, false otherwise
     */
    public static void addWebSocketRoute(String url, Class<? extends MangooWebSocket> controllerClass, boolean requireAuthentucation) {
        Objects.requireNonNull(url, Required.URL.toString());
        Objects.requireNonNull(controllerClass, Required.CONTROLLER_CLASS.toString());
        
        Route route = new Route(RouteType.WEBSOCKET).toUrl(url).withClass(controllerClass).withAuthentication(requireAuthentucation);
        Router.addRoute(route);
    }

    /**
     * Adds a new File route to the router
     * @param url The URL of the route
     */
    public static void addFileRoute(String url) {
        Objects.requireNonNull(url, Required.URL.toString());
        
        Route route = new Route(RouteType.RESOURCE_FILE).toUrl(url);
        Router.addRoute(route);
    }

    /**
     * Adds a new Path route to the router
     * @param url The URL of the route
     */
    public static void addPathRoute(String url) {
        Objects.requireNonNull(url, Required.URL.toString());
        
        Route route = new Route(RouteType.RESOURCE_PATH).toUrl(url);
        Router.addRoute(route);
    }
}