package com.server.app;

/**
 * @author Kazi Tanvir Azad
 */
public class Launcher {
    public static void main(String[] args) {
        MockServerApp.main(args);
        // Adding custom shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // executing shutdown processes
            // run gc
            Runtime.getRuntime().gc();
        }));
    }
}
