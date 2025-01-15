package fr.uge.xplain.services;

import fr.uge.xplain.ExplanationEntity;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@HelidonTest
class DataBaseServiceTest {

    private static ExplanationEntity createExplanation(LocalDate creationDate) {
        var explanation = new ExplanationEntity();
        explanation.setSourceCode("public class Test {}");
        explanation.setExplanations("This is a simple test class.");
        explanation.setCreationDate(creationDate);
        return explanation;
    }

    @Inject
    private DataBaseService dataBaseService;

    @PersistenceContext(unitName = "explanationsDbPU")
    private EntityManager entityManager;

    @Test
    @Transactional
    void testSetDataBaseData() {
        var explanation = createExplanation(LocalDate.now());
        // Persist the entity
        dataBaseService.setDataBaseData(explanation);
        // Retrieve the entity from the database
        var retrievedExplanation = dataBaseService.getExplanationById(explanation.getId());
        assertAll(
                () -> assertEquals("public class Test {}", retrievedExplanation.getSourceCode()),
                () -> assertEquals("This is a simple test class.", retrievedExplanation.getExplanations())
        );
        dataBaseService.deleteDataBaseData(explanation.getId());
    }

    @Test
    @Transactional
    void testDeleteDataBaseData() {
        // Add an entity to delete
        var explanation = createExplanation(LocalDate.now());
        dataBaseService.setDataBaseData(explanation);
        // Ensure it exists
        var retrievedExplanation = dataBaseService.getExplanationById(explanation.getId());
        assertNotNull(retrievedExplanation, "Entity should exist before deletion");
        // Delete the entity
        var deleted = dataBaseService.deleteDataBaseData(explanation.getId());
        // Verify that it has been deleted
        assertAll(
                () -> assertTrue(deleted, "Entity should be deleted"),
                () -> assertThrows(NoResultException.class, () -> dataBaseService.getExplanationById(explanation.getId()))
        );
        dataBaseService.deleteDataBaseData(explanation.getId());
    }

}