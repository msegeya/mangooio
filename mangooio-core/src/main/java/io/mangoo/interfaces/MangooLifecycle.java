package io.mangoo.interfaces;

/**
 *
 * @author svenkubiak
 *
 */
public interface MangooLifecycle {
    
    /**
     * Executed prior to initializing the routes and used for setup of
     * Server Sent Event-, WebSocket-, File- and Path-Routes
     */
    public void routing();
    
    /**
     * Executed after config is loaded and injector is initialized
     *
     */
    void applicationInitialized();

    /**
     * Executed after the application is completely started
     */
    void applicationStarted();
    
    /**
     * Executed after forcible signal of JVM shutdown has been send
     */
    void applicationStopped();
}