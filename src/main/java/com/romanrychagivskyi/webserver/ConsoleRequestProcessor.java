package com.romanrychagivskyi.webserver;

import java.net.Socket;

/**
 * Simple console request processor
 */
public class ConsoleRequestProcessor implements RequestProcessor {

    public void process(Socket socket) {
        System.out.print("Print to console");
    }
}
