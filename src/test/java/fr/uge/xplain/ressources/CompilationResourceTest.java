package fr.uge.xplain.ressources;

import fr.uge.xplain.requests.CodeRequest;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

@HelidonTest
class CompilationResourceTest {

    @Inject
    WebTarget webTarget;

    private Response serverResponse(String sourceCode) {
        var codeRequest = new CodeRequest(sourceCode);
        var entity = Entity.json(codeRequest);
        return webTarget.path("/compile")
                .request()
                .buildPost(entity)
                .invoke();
    }

    @Test
    void compileSuccess() {
        var sourceCode = "public class FamousClass {}";
        Response.Status actual;
        try (var response = serverResponse(sourceCode)) {
            var code = response.getStatus();
            actual = Response.Status.fromStatusCode(code);
        }
        assertEquals(OK, actual);
    }

    @Test
    void compileFailure() {
        var sourceCode = "public classs FamousClass {}";
        Response.Status actual;
        try (var response = serverResponse(sourceCode)) {
            var code = response.getStatus();
            actual = Response.Status.fromStatusCode(code);
        }
        assertEquals(BAD_REQUEST, actual);
    }

}