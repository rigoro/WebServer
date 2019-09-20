package com.romanrychagivskyi.webserver;

import java.net.Socket;

/**
 * Http handler thread, wraps call to according application processor
 */
public class HttpRequestHandler implements Runnable {

    private final Socket socket;
    private final RequestProcessor processor;

    HttpRequestHandler(Socket socket, RequestProcessor processor) {
        this.socket = socket;
        this.processor = processor;
    }

    /**
     * Starts thread to process request
     */
    @Override
    public void run() {
        try {
            processor.process(socket);
        } catch (Exception ex) {
            System.out.println("Processing failure");
        }
    }
}
