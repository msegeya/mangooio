package io.mangoo.test.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author svenkubiak
 *
 */
public class ConcurrentRunner {
    private Runnable runnable;
    private int threads = 10;
    private volatile Error error;
    private volatile RuntimeException runtimeException;
    
    public static ConcurrentRunner create() {
        return new ConcurrentRunner();
    }

    /**
     * Adds the runnable to the runner
     * 
     * @param runnable The runnable to execute
     * @return ThreadRunner instance
     */
    public ConcurrentRunner withRunnable(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }
    
    /**
     * Sets the number of threads to used when executed, defaults to 10
     * 
     * @param threads The number of threads
     * @return ThreadRunner instance
     */
    public ConcurrentRunner withThreads(int threads) {
        this.threads = threads;
        return this;
    }
    
    /**
     * Starts instances of the given runnable depending on the
     * number of threads to execute
     * 
     * @throws InterruptedException if the runnable throws an error
     */
    public void run() throws InterruptedException {
        List<Thread> runnableThreads = new ArrayList<>();
        for (int i=0; i < threads; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        runnable.run();
                    } catch (Error e) {
                        error = e;
                    } catch (RuntimeException e) {
                        runtimeException = e;
                    }
                }
            });
            runnableThreads.add(thread);
        }
        
        runnableThreads.parallelStream().forEach(thread -> {
            thread.run();
            if (error != null) {
                throw error;                
            }
            if (runtimeException != null) {
                throw runtimeException;                
            }
        });
    }
}