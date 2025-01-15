package fr.uge.xplain.resources;

import fr.uge.xplain.ExplanationEntity;
import fr.uge.xplain.requests.ResponseToObject;
import fr.uge.xplain.services.DataBaseService;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.Objects;

/**
 * This resource handles HTTP requests for managing explanations in the database.
 * It provides endpoints for CRUD operations, including retrieving, adding, and deleting explanations.
 */
@Path("/database")
public final class DataBaseResource {

    @Inject
    private DataBaseService dataBaseService;

    /**
     * Retrieves all explanations from the database.
     * If no data is found, returns a "not found" response.
     *
     * @return a Response object containing the database data or a message if no data exists
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve all data from the database",
            description = "Fetches all explanations stored in the database. If no data exists, a 'not found' message is returned.")
    @APIResponse(description = "Database data in JSON format or 'not found' message",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExplanationEntity.class)))
    public Response getDatabaseData() {
        var data = dataBaseService.getDataBaseData();
        if (!data.isEmpty()) {
            return Response.ok(new ResponseToObject<ExplanationEntity>(data)).build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }

    /**
     * Retrieves a specific explanation by its ID.
     *
     * @param id the ID of the explanation to retrieve
     * @return a Response object containing the explanation or a 'not found' message
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve an explanation by ID",
            description = "Fetches a specific explanation from the database using its ID. Returns a 'not found' message if the explanation does not exist.")
    @APIResponse(description = "Explanation data in JSON format or 'not found' message",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExplanationEntity.class)))
    public Response getExplanationById(@PathParam("id") int id) {
        try {
            var explanation = dataBaseService.getExplanationById(id);
            return Response.ok(explanation).build();
        } catch (NoResultException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    /**
     * Deletes an explanation by its ID.
     *
     * @param id the ID of the explanation to delete
     * @return a Response indicating success or failure of the deletion operation
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete an explanation by ID",
            description = "Deletes a specific explanation from the database using its ID. Returns 'not found' if the explanation does not exist.")
    public Response deleteExplanationById(@PathParam("id") int id) {
        if (dataBaseService.deleteDataBaseData(id)) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    /**
     * Adds a new explanation to the database.
     *
     * @param explanation the ExplanationEntity object to add to the database
     * @return a Response indicating the creation status
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a new explanation",
            description = "Adds a new explanation to the database.")
    @APIResponse(description = "Response indicating creation status")
    public Response addData(ExplanationEntity explanation) {
        Objects.requireNonNull(explanation);
        dataBaseService.setDataBaseData(explanation);
        return Response.status(Response.Status.CREATED).build();
    }

}