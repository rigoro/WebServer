package com.romanrychagivskyi.webserver;

import java.net.Socket;

/**
 * Request processor interface.
 * Allows implementation of different processor statuses
 */
public interface RequestProcessor {

    void process(Socket socket);
}
