package pyttewebserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * A simple server that can listen on a port. Incoming requests are sent back to
 * the client. Based on socket exercise 1.
 *
 * @author Thomas Ejnefjäll
 * @author Kristoffer Freiholtz
 */
public class PytteServer {

    private final int mPort;

    /**
     * Constructs a simple server that can listen to a port.
     *
     * @param port The port number that the server can listen to
     */
    public PytteServer(int port) {
        mPort = port;
    }

    /**
     * The server starts listening on a port. Incoming requests are handled and
     * the proper response is sent
     *
     * @throws IOException In case of a IO problem with the socket
     */
    public void runServer() throws Exception {
        ServerSocket serverSocket = new ServerSocket(mPort);
        while (true) {
            Socket clientHandlerSocket = serverSocket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientHandlerSocket.getInputStream()));
            DataOutputStream writer = new DataOutputStream(clientHandlerSocket.getOutputStream());
            String request = reader.readLine();
            System.out.println(request);
            
            if (request != null) {
                ResponseFile responseFile = handleRequest(request);
                if (!responseFile.exists()) {
                    responseFile = new ResponseFile("error404.html", responseFile.getRequestType(), responseFile.getHTTPType());
                }
                System.out.println(request);
                if (responseFile.getHTTPType().equals("1.1")) {
                    String response;
                    response = "HTTP/1.1 " + responseFile.getStatusCode() + "\r\nServer: 127.0.0.1:" + mPort + "\r\nDate: " + responseFile.getDate()
                                + "\r\nContent-Type: " + responseFile.getContentType() + "\r\nContent Length: " + responseFile.getContentLength();
                    if (responseFile.getStatusCode().startsWith("2")) {                       
                        response += "\r\nLast Modified: " + responseFile.getLastModified();
                    }
                    response += "\r\nConnection: close" + "\r\n\r\n";
                    writer.writeBytes(response);
                }
                writer.write(responseFile.getBytes());
            }
            clientHandlerSocket.close();
        }
    }

    /**
     * Takes a string request a removes the beginning and the end of a valid
     * request
     *
     * @param request The string we want to alter
     * @return the essential part of the request
     */
    private ResponseFile handleRequest(String request) {
        if (request.endsWith("HTTP/1.1") || request.endsWith("HTTP/1.0")) {
            if (request.startsWith("GET /")) {
                return new ResponseFile(request.substring(5, request.length() - 9), "GET", "1.1");
            } else if (request.startsWith("HEAD /")) {
                return new ResponseFile(request.substring(6, request.length() - 9), "HEAD", "1.1");
            } else {
                return new ResponseFile("error400.html", "GET", "1.1");
            }
        } else {// Handles HTTP 0.9 requests, only GET requests are valid
            if (request.startsWith("GET /")) {
                return new ResponseFile(request.substring(5, request.length()), "GET", "0.9");
            } else {
                return new ResponseFile("error400.html", "GET", "1.1");
            }
        }
    }

    /**
     * Returns the correct response from the given string
     *
     * @param returnRequest The string that decide what response to give
     * @return The requested file
     * @throws IOException If there was an I/O error while reading from the
     * stream
     *
     * G?R ALLT H?R ANNARS BLIR DET EXCEPTIONS EFTERSOM DET SKICAKS NULL
     */
//    private byte[] sendBack(String returnRequest) throws IOException {
//        ResponseFile file = new ResponseFile(returnRequest);    
//        if (file.exists()) {
//            file.existingFile();
//            mDate = getServerTime();
//            mLastModified = getLastModified(returnRequest);
//            mNrBytes = Files.readAllBytes(Paths.get(returnRequest)).length;
//            mLastModified = getLastModified(returnRequest);
//            mType = Files.probeContentType(Paths.get(returnRequest));
//            System.out.println(returnRequest);
//            return Files.readAllBytes(Paths.get(returnRequest));          
//        }
//        else{
//            file.error404();
//        }
//    
//        mNrBytes = Files.readAllBytes(Paths.get("error.html")).length;
//        return Files.readAllBytes(Paths.get("error.html"));
//    }
}
