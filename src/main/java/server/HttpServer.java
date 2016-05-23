package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class HttpServer {
    private static final Logger LOGGER = Logger.getLogger(HttpServer.class.getName());
    private final int port;
    private final Path fileDirectory;
    private ServerSocket socket;
    private ExecutorService executorService;
    private boolean serverRunning = false;

    public HttpServer(int port, String fileDirectory) throws IOException {
        this.port = port;
        this.fileDirectory = getFileDirectoryFrom(fileDirectory);
    }

    public void start() {
        serverRunning = true;
        try {
            socket = new ServerSocket(port);
            executorService = Executors.newFixedThreadPool(idealNumberOfThreads());
            LOGGER.info(String.format("Server started and available at %s:%s", System.getenv("HOSTNAME"), port));
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }

        while (isServerRunning()) {
            try {
                executorService.execute(new Thread(new SocketConnection(socket.accept(), this)));
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }
        stop();
    }

    public void stop() {
        serverRunning = false;
        shutdownThreadPoolAndWait(1, TimeUnit.MINUTES);
    }

    /**
     * Using the current number of processors multiplied by a relatively large number(8), <br>
     * because most of the waiting involves external resources <br>
     * as stated by <b>OCP Oracle® Certified Professional Java® SE 8 Programmer II Chapter 7. Concurrency</b>
     *
     * @return the number of ideal threads for machine running the server
     */
    private int idealNumberOfThreads() {
        return Runtime.getRuntime().availableProcessors() * 8;
    }

    /**
     * If a stringy representation of a file path is not provided
     * then a path from the user home directory is automagically returned
     *
     * @param fileDirectory <b><code>String</code></b>
     * @return the <b><code>Path</code></b> object that points to the file directory
     */
    private Path getFileDirectoryFrom(String fileDirectory) throws IOException {
        Path homeDirectory;
        if (fileDirectory == null) {
            homeDirectory = Paths.get(System.getProperty("user.home"), "serverFiles");
        } else {
            homeDirectory = Paths.get(fileDirectory);
        }
        if (!Files.exists(homeDirectory)) {
            Files.createDirectory(homeDirectory);
        }
        return homeDirectory;
    }

    /**
     * Stops the <b><code>ExecutorService</code></b> and waits the specified number of minute for submitted request to finish
     *
     * @param minutes  <b><code>int</code></b>
     * @param timeUnit <b><code>TimeUnit</code></b>
     */
    private void shutdownThreadPoolAndWait(int minutes, TimeUnit timeUnit) {
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(minutes, timeUnit)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                LOGGER.severe("Termination of server was interrupted");
            }
        }
    }

    public Path getFileDirectory() {
        return fileDirectory;
    }

    public boolean isServerRunning() {
        return serverRunning;
    }
}
