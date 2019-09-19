package com.romanrychagivskyi.webserver;

import java.net.Socket;

public interface RequestProcessor {

    void process(Socket socket);
}
