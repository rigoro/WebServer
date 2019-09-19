package com.romanrychagivskyi.webserver;

import java.util.Scanner;

/**
 * Main class that starts Web Server
 * Correct root directory has to be specified for file searching
 */
public class ApplicationRunner {

    private static final int THREAD_COUNT = 10;

    //Please specify correct path to some local folder to make it working
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter local directory path:");
        while (sc.hasNextLine()) {
            System.out.println("Please enter local directory path:");
            String rootPath = sc.nextLine();
            System.out.println("Please enter server port:");
            String port = sc.nextLine();
            new Thread(new WebServer(Integer.parseInt(port), THREAD_COUNT, new FileRequestProcessor(rootPath))).start();
            System.out.println("Server started, start calling it [localhost:port/filename.ext]");
        }
    }
}
