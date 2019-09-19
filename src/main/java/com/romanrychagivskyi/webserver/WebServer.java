package com.romanrychagivskyi.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Multi-threaded web server with thread pooling
 */
public class WebServer implements Runnable {

    private final static int DEFAULT_POOL_SIZE = 10;

    private final int port;
    private final int poolSize;
    private final RequestProcessor processor;

    private boolean isStopped;

    private ExecutorService threadPool;

    WebServer(int port, int poolSize, RequestProcessor processor) {
        this.port = port;
        this.poolSize = poolSize;
        this.processor = processor;
    }

    WebServer(int port) {
        this(port, DEFAULT_POOL_SIZE, new ConsoleRequestProcessor());
    }

    /**
     * Main thread, initializes thread pool, establishes socket connections and handles http requests
     */
    @Override
    public void run() {
        threadPool = Executors.newFixedThreadPool(poolSize);

        ServerSocket server;
        try {
            //establish socket connection
            server = new ServerSocket(port);
        } catch (IOException e) {
            throw new WebServerException("Cannot open the port " + port, e);
        }

        while (!isStopped) {
            try {
                //start accepting client requests
                Socket socket = server.accept();
                //start new thread to execute UI request
                threadPool.execute(new Thread(new HttpRequestHandler(socket, processor)));
            } catch (IOException e1) {
                if (isStopped) {
                    System.out.print("Server has stopped");
                    return;
                }
                throw new WebServerException("Can not connect to client");
            }

        }

    }

    public synchronized void stop() {
        isStopped = true;
    }
}
