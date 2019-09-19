package com.romanrychagivskyi.webserver;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class WebServerTest {

    private static WebServer server;
    private static Thread mainThread;
    private static final String HOST = "localhost";
    private static final int SERVER_PORT = 8001;
    private static final int THREADS_COUNT = 10;
    private static final String BASE_URL = "http://" + HOST + ":" + SERVER_PORT + "/";

    @ClassRule
    public static final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @BeforeClass
    public static void setUp() throws IOException {
        server = new WebServer(SERVER_PORT, THREADS_COUNT, new FileRequestProcessor(getRootPath()));
        mainThread = new Thread(server);
        mainThread.start();
    }


    @Test
    public void testHead() throws IOException {
        HttpHead head = new HttpHead(BASE_URL);
        HttpResponse response = HttpClientBuilder.create().build().execute(head);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.getStatusCode());
    }

    @Test
    public void testGetFileDoesNotExist() throws IOException {
        HttpGet get = new HttpGet(BASE_URL + "/invalidFile");
        HttpResponse response = HttpClientBuilder.create().build().execute(get);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testGetDirectory() throws IOException {
        temporaryFolder.newFolder("testFolder");
        HttpGet get = new HttpGet(BASE_URL + "/testFolder");
        HttpResponse response = HttpClientBuilder.create().build().execute(get);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.FORBIDDEN.getStatusCode());
    }

    @Test
    public void testGetFileWithRestrictedExtension() throws IOException {
        temporaryFolder.newFile("testFile.bat");
        HttpGet get = new HttpGet(BASE_URL + "/testFile.bat");
        HttpResponse response = HttpClientBuilder.create().build().execute(get);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.FORBIDDEN.getStatusCode());
    }

    @Test
    public void testGetFilePositiveScenario() throws IOException {
        temporaryFolder.newFile("testFile.txt");
        HttpGet get = new HttpGet(BASE_URL + "/testFile.txt");
        HttpResponse response = HttpClientBuilder.create().build().execute(get);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.getStatusCode());
    }


    @AfterClass
    public static void tearDown() {
        server.stop();
        mainThread.interrupt();
    }

    private static  String getRootPath() {
        File rootFolder = temporaryFolder.getRoot();
        return rootFolder.toPath() + "/";
    }
}
