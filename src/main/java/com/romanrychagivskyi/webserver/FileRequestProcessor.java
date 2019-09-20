package com.romanrychagivskyi.webserver;

import java.io.*;
import java.net.Socket;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main file processor class
 * File is searched only in a current directory
 * Two methods are supported (HEAD|GET)
 *
 * @FileExtensionAndType provides all supported file extensions
 */
public class FileRequestProcessor implements RequestProcessor {

    private static final int FILE_SEND_BUFFER_SIZE = 4096;
    private static final Pattern METHOD_URL_VERSION_PATTERN = Pattern.compile("(GET|HEAD) ([^ ]+) HTTP/(\\d\\.\\d)");
    private static final Pattern HEADERS_PATTERN = Pattern.compile("([^:]*):\\s*(.*)");
    private static final String HTTP_PROTOCOL = "HTTP/";
    private static final String VERSION11 = "1.1";
    private static final String VERSION10 = "1.0";
    private final String root;

    FileRequestProcessor(String root) {
        this.root = root;
    }

    /**
     * Method reads parameters from input stream,
     * retrieves file from local system and return bytes into output stream
     *
     * @param socket
     */
    public void process(Socket socket) {
        PrintStream out = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));

            String line = in.readLine();
            logRequest(socket, line);

            if (line == null) {
                throw new WebServerException("Connection closed");
            }

            //using matcher to extract method, filename, http version
            Matcher matcher = METHOD_URL_VERSION_PATTERN.matcher(line);
            if (!matcher.matches()) {
                System.out.println("Invalid request parameters");
                out.print(errorResponse("Invalid request parameters", HttpStatus.BAD_REQUEST, VERSION10));
                return;
            }

            String method = matcher.group(1);
            String uri = matcher.group(2);
            String version = matcher.group(3);

            //only two http protocol versions supported 1.1 and 1.0
            if (!version.equals(VERSION10) && !version.equals(VERSION11)) {
                out.print(errorResponse("Http version " + version + " is not implemented", HttpStatus.NOT_IMPLEMENTED, VERSION10));
                return;
            }

            if (method == null) {
                out.print(errorResponse("Unsupported method type", HttpStatus.BAD_REQUEST, version));
                return;
            }

            //specifying default content type
            String contentType = FileExtensionAndType.HTML.getContentType();

            //composing full file path on a local system
            String filename = root + "/" + uri.replaceAll("^/+", "");
            if (method.equals(HttpMethod.GET.name())) {

                if (!new File(filename).exists()) {
                    out.print(errorResponse("File is not found", HttpStatus.NOT_FOUND, version));
                    return;
                }

                if (new File(filename).isDirectory()) {
                    out.print(errorResponse("Requested resource is directory", HttpStatus.FORBIDDEN, version));
                    return;
                }

                if (!FileExtensionAndType.isAllowed(filename)) {
                    out.print(errorResponse("Requested resource is forbidden", HttpStatus.FORBIDDEN, version));
                    return;
                }

                contentType = FileExtensionAndType.getContentType(filename);
            }

            //extracting client headers to be able to send it back
            Map<String, String> headers = extractHeaders(in);
            boolean keepAlive = keepAlive(headers, version);

            out.print(createResponseContent(contentType, headersToString(headers), version, keepAlive));

            if (method.equals(HttpMethod.GET.name())) {
                transferFileToClient(new FileInputStream(filename), out);
            }
        } catch (IOException ex) {
            if (out != null) {
                out.print(errorResponse("", HttpStatus.INTERNAL_SERVER_ERROR, VERSION10));
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Extracts header parameters
     *
     * @param reader
     * @return
     * @throws IOException
     */
    private Map<String, String> extractHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();

        String headerLine = reader.readLine();
        while (!headerLine.equals("")) {
            Matcher matcher = HEADERS_PATTERN.matcher(headerLine);
            if (matcher.matches()) {
                headers.put(matcher.group(1), matcher.group(2).toLowerCase());
            }
            headerLine = reader.readLine();
        }
        return headers;
    }

    /**
     * Returns keep alive flag based on request parameters and http version
     *
     * @param headers
     * @param version
     * @return
     */
    private boolean keepAlive(Map<String, String> headers, String version) {
        boolean isAlive = headers.containsKey("Connection") && headers.get("Connection").contains("keep-alive");
        boolean isClosed = headers.containsKey("Connection") && headers.get("Connection").contains("close");
        return VERSION10.equals(version) && isAlive || VERSION11.equals(version) && !isClosed;
    }


    /**
     * Sends file bytes to output stream
     *
     * @param inputStream
     * @param out
     * @throws IOException
     */
    private static void transferFileToClient(InputStream inputStream, OutputStream out) throws IOException {
        byte[] a = new byte[FILE_SEND_BUFFER_SIZE];
        int n;
        while ((n = inputStream.read(a)) > 0) {
            out.write(a, 0, n);
        }
    }

    /**
     * Logs request details
     *
     * @param socket
     * @param line
     */
    private void logRequest(Socket socket, String line) {
        System.out.println("Received request on " + Calendar.getInstance().getTime() + " address=" + socket.getInetAddress() + " port=" + socket.getPort() + " header line " + line);
    }

    /**
     * Forms error response
     *
     * @param msg
     * @param status
     * @param version
     * @return
     */
    private String errorResponse(String msg, HttpStatus status, String version) {
        return HTTP_PROTOCOL + version + ' ' + status.getStatusCode() + " " + status.getStatusMsg() + "\r\n" +
                "Content-type: text/html\r\n\r\n" +
                "<html><head></head><body><h1>Error Msg: " + msg + "</body></html>\n";

    }

    /**
     * Transforms headers Map into string value
     *
     * @param headers
     * @return
     */
    private String headersToString(Map<String, String> headers) {
        StringBuilder headerBuilder = new StringBuilder();
        for (String key : headers.keySet()) {
            headerBuilder.append(key).append(":").append(headers.get(key)).append("\r\n");
        }
        return headerBuilder.toString();
    }

    /**
     * Creates response content
     *
     * @param contentType
     * @param headers
     * @param version
     * @param keepAlive
     * @return
     */
    private String createResponseContent(String contentType, String headers, String version, boolean keepAlive) {
        String keepAliveParameter = "";
        if (keepAlive && VERSION10.equals(version))
            keepAliveParameter = "Connection: keep-alive\r\n";
        else if (!keepAlive && VERSION11.equals(version))
            keepAliveParameter = "Connection: close\r\n";

        return HTTP_PROTOCOL + version + ' ' + HttpStatus.OK.getStatusCode() + "\r\n" +
                headers + keepAliveParameter +
                "Content-type: " + contentType + "\r\n\r\n";
    }
}
