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

public class HttpServerImpl implements HttpServer {
    private static final Logger LOGGER = Logger.getLogger(HttpServerImpl.class.getName());
    private final int port;
    private final Path fileDirectory;
    private ServerSocket socket;
    private ExecutorService executorService;
    private ResponseDispatcher dispatcher;
    private boolean serverRunning = false;

    public HttpServerImpl(int port, String fileDirectory) throws IOException {
        socket = new ServerSocket(port);
        this.port = socket.getLocalPort();
        this.fileDirectory = getFileDirectoryFrom(fileDirectory);
        this.dispatcher = new ResponseDispatcherImpl(this);
    }

    @Override
    public void start() {
        serverRunning = true;
        executorService = Executors.newFixedThreadPool(idealNumberOfThreads());
        LOGGER.info(String.format("Server started and available at %s:%s", System.getenv("HOSTNAME"), socket.getLocalPort()));

        while (isServerRunning()) {
            try {
                executorService.execute(new Thread(new SocketConnection(socket.accept(), this)));
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
            }
        }
        stop();
    }

    @Override
    public void stop() {
        serverRunning = false;
        shutdownThreadPoolAndWait(1, TimeUnit.MINUTES);
        System.exit(0);
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
     * then a path(/httpServerFiles) from the user home directory is automagically returned
     *
     * @param fileDirectory <b><code>String</code></b>
     * @return the <b><code>Path</code></b> object that points to the file directory
     */
    private Path getFileDirectoryFrom(String fileDirectory) throws IOException {
        Path homeDirectory;
        String dirMessage;
        if (fileDirectory == null) {
            homeDirectory = Paths.get(System.getProperty("user.home"), "httpServerFiles");
            dirMessage = "File location defaulted to %s";
        } else {
            homeDirectory = Paths.get(fileDirectory);
            dirMessage = "File location is %s";
        }
        if (!Files.exists(homeDirectory)) {
            Files.createDirectory(homeDirectory);
        }

        LOGGER.info(String.format(dirMessage, homeDirectory.toAbsolutePath()));
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
                LOGGER.severe("Termination of server was interrupted. Exiting now");
            }
        }
    }

    @Override
    public Path getFileDirectory() {
        return fileDirectory;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean isServerRunning() {
        return serverRunning;
    }

    @Override
    public ResponseDispatcher getDispatcher() {
        return dispatcher;
    }
}
