package conf;

import com.google.inject.Singleton;

import controllers.WebSocketController;
import io.mangoo.interfaces.MangooLifecycle;
import io.mangoo.routing.Router;

@Singleton
public class Lifecycle implements MangooLifecycle {

    @Override
    public void routing() {
        Router.addWebSocketRoute("/websocket", WebSocketController.class, false);
        Router.addWebSocketRoute("/websocketauth", WebSocketController.class, true);
        Router.addServerSentEventRoute("/sse", false);
        Router.addServerSentEventRoute("/sseauth", true);
        Router.addPathRoute("/assets/");
        Router.addFileRoute("/robots.txt");
    }
    
    @Override
    public void applicationInitialized() {
        // Do nothing for now
    }

    @Override
    public void applicationStarted() {
        // Do nothing for now
    }

    @Override
    public void applicationStopped() {
        // Do nothing for now
    }
}