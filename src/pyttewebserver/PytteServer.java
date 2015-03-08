package pyttewebserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple server that can listen on a port. Incoming requests are sent back to
 * the client. Based on socket exercise 1.
 *
 * @author Thomas Ejnefjäll
 * @author Kristoffer Freiholtz
 */
public class PytteServer {

    private int mPort;
    private String[] mResponses = {"index.html", "pictures.htm", "error.html", "truck.gif", "text.txt"};
    private String mRequest;
    private String mConnection;
    private int mNrBytes;
    private String mType;
    private String mLastModified;
    private String mDate;

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
            readRequest(reader);
            System.out.println(mRequest);
            mRequest = handleRequest(mRequest);
            System.out.println(mRequest);
            DataOutputStream writer = new DataOutputStream(clientHandlerSocket.getOutputStream());

            byte[] bytesArray = sendBack(mRequest);
            writer.writeBytes("HTTP/1.1 200 OK\r\nServer: 127.0.0.1:" + mPort + "\r\nDate: " + mDate + "\r\nContent Length: " + mNrBytes + "\r\nContent-Type: " + mType + 
                    "\r\nLast Modified: " + mLastModified + "\r\nConnection: " + mConnection + "\r\n\r\n");

            writer.write(sendBack(mRequest));
            clientHandlerSocket.close();
        }
    }
    
    private void readRequest(BufferedReader reader) throws IOException{
        mRequest = reader.readLine();
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
                if (request.endsWith("HTTP/1.1") || request.endsWith("HTTP/1.0")) {
                    return request.substring(5, request.length() - 9);
                } else {
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
     * 
     * G?R ALLT H?R ANNARS BLIR DET EXCEPTIONS EFTERSOM DET SKICAKS NULL
     */
    private byte[] sendBack(String returnRequest) throws IOException {
        for (String response : mResponses) {
            if (response.equals(returnRequest)) {
                mDate = getServerTime();
                mLastModified = getLastModified(response);
                mNrBytes = Files.readAllBytes(Paths.get(response)).length;
                mLastModified = getLastModified(response);
                mType = Files.probeContentType(Paths.get(response));
                return Files.readAllBytes(Paths.get(response));
            }
        }
        mNrBytes = Files.readAllBytes(Paths.get("error.html")).length;
        return Files.readAllBytes(Paths.get("error.html"));
    }
    
    
    /**
     * Returns the correct response from the given string
     *
     * @return 
     * 
     * @author Hannes R from stackoverflow.com (Googled solution for getting system time)
     */
    String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }
    
    String getLastModified(String response) {
        File file = new File(response);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(file.lastModified());
    }
}
    