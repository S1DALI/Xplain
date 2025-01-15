package fr.uge.xplain;

import fr.uge.xplain.resources.CompilationResource;
import fr.uge.xplain.resources.DataBaseResource;
import io.helidon.microprofile.server.Server;

/**
 * The Main class is the entry point of the application. It initializes the server
 * and registers the necessary resources to handle incoming HTTP requests.
 * <p>
 * This class uses Helidon MicroProfile to build and start an HTTP server.
 * Resources related to database, compilation, and large language models are registered
 * as part of the server.
 * </p>
 */
public final class Main {

    /**
     * The main method is the entry point of the application. It starts the server
     * and prints the URL where the server is accessible.
     *
     * @param args Command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        // Start the Helidon MP server
        var server = startServer();
        // Print the server's URL
        System.out.println("Server started at: http://localhost:" + server.port());
    }

    /**
     * Starts the Helidon MicroProfile server and registers the necessary resource
     * classes to handle HTTP requests.
     *
     * @return The started Helidon server instance
     */
    static Server startServer() {
        // Registers the resource classes with the server
        return Server.builder()
                .addResourceClass(DataBaseResource.class)
                .addResourceClass(CompilationResource.class)
                .build()
                .start();
    }

}