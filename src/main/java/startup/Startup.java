package startup;

import server.HttpServer;
import server.HttpServerImpl;

import java.util.logging.Logger;

public class Startup {
    private static final Logger LOGGER = Logger.getLogger(Startup.class.getName());

    public static void main(String[] args) {
        HttpServer httpServer;
        String webRoot = null;
        int port = 0;
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("-help")) {
            LOGGER.info("Server started with default configuration.");
        } else {
            port = Integer.parseInt(args[0]);
            if (args.length > 1) {
                webRoot = args[1];
            }
        }

        try {
            httpServer = new HttpServerImpl(port, webRoot);

            httpServer.start();
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }

    }
}
