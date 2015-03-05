package pyttewebserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A simple server that can listen on a port. Incoming requests are sent back to
 * the client. Based on socket exercise 1.
 *
 * @author Thomas Ejnefjäll
 * @author Kristoffer Freiholtz
 */
public class PytteServer {

    private int mPort;
    String responses[] = {"index.html", "pictures.htm", "error.html", "truck.gif", "text.txt"};

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
            String request;
            Socket clientHandlerSocket = serverSocket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientHandlerSocket.getInputStream()));
            request = reader.readLine();
            System.out.println(request);
            request = handleRequest(request);
            System.out.println(request);
            DataOutputStream writer = new DataOutputStream(clientHandlerSocket.getOutputStream()); 
            writer.writeBytes("HTTP/1.1 200 OK\r\nServer: 127.0.0.1\r\nDate: Yesterday\r\n\r\n");
            writer.write(sendBack(request));
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
    private String handleRequest(String request) {
        if (request != null) { // request.startsWith cant handle null.
            if (request.startsWith("GET /")) {
                if(request.endsWith("HTTP/1.1") || request.endsWith("HTTP/1.0")){
                    return request.substring(5, request.length() - 9);
                }
                else{
                    return request.substring(5, request.length());
                }
            }
        }
        return null;
    }

    /**
     * Returns the correct response from the given string
     *
     * @param returnRequest The string that decide what response to give
     * @return The requested file
     * @throws IOException If there was an I/O error while reading from the
     * stream
     */
    private byte[] sendBack(String returnRequest) throws IOException {
        for (String response : responses) {
            if (response.equals(returnRequest)) {
                return Files.readAllBytes(Paths.get(response));
            }
        }
        return Files.readAllBytes(Paths.get("error.html"));
    }
}
