package pyttewebserver;

/**
 * Based on socket exercise 1.
 * 
 * @author Thomas Ejnefjäll
 * @author Kristoffer Freiholtz
 */
public class PytteServerMain {

    /**
     * Program entry point. Usage: java PytteServer [port]
     * @param args custom port from the user
     */
    public static void main(String[] args) {
        int port = 8080;

        if (args.length > 0 && Integer.valueOf(args[0]) > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException nfe) {
                System.out.println("Non integer value for port number, using " + port + " instead");
            }
        } else {
            System.out.println("Value less than 0, using " + port + " instead");
        }
        
        PytteServer server = new PytteServer(port);
        System.out.println("Starting server listening on port " + port);
        try {
            server.runServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
