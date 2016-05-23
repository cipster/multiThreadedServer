package startup;

import server.HttpServer;

import java.util.logging.Logger;

public class Mains {
    private static final Logger LOGGER = Logger.getLogger(Mains.class.getName());

    public static void main(String[] args) {
        String webRoot = null;
        int port = 0;
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("-help")) {
            LOGGER.info("Usage: java -cp WebServer.jar web.Server <port> <web root> ");
            System.exit(1);
        } else {
            port = Integer.parseInt(args[0]);
            if (args.length > 1) {
                webRoot = args[1];
            }
        }

        try {
            new HttpServer(port, webRoot).start();
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }

    }
}
