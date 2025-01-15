package fr.uge.xplain.resources;

import fr.uge.xplain.requests.CodeRequest;
import fr.uge.xplain.requests.ResponseToObject;
import fr.uge.xplain.services.CompilationService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.Map;
import java.util.Objects;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * This resource handles HTTP requests related to code compilation.
 * It provides an endpoint to receive source code, compile it,
 * and return the compilation results as a JSON response.
 */
@Path("/compile")
public final class CompilationResource {

    /**
     * Compiles the provided source code and returns the result as a JSON response.
     * If there are errors during compilation, they will be returned as a JSON object with error details.
     *
     * @param codeRequest the request containing the source code to compile
     * @return a Response object containing the compilation result or errors
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Compiles the provided source code",
            description = "Compiles the source code provided in the request body and returns a JSON response with the result or errors.")
    @APIResponse(description = "Compilation result or error details in JSON format",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseToObject.class)))
    @Produces(MediaType.APPLICATION_JSON)
    public Response compile(CodeRequest codeRequest) {
        Objects.requireNonNull(codeRequest);
        var sourceCode = codeRequest.code();
        var errors = CompilationService.compile(sourceCode);
        if (errors.isEmpty()) {
            return Response.ok()
                    .entity(Map.of("message", "Compilation réussie !"))
                    .build();
        }
        var errorResponse = new ResponseToObject<>(errors);
        return Response.status(BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }

}