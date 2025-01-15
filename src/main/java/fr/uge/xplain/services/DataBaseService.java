package fr.uge.xplain.services;

import fr.uge.xplain.ExplanationEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Service that manages access to database data related to {@link ExplanationEntity} entities.
 * This service is used to perform CRUD (Create, Read, Delete) operations on the database.
 * It uses the Jakarta Persistence framework to interact with the database.
 */
@ApplicationScoped
public class DataBaseService {

    /**
     * The EntityManager is used to perform persistence operations with the database.
     * It is automatically injected by the CDI (Contexts and Dependency Injection) container.
     */
    @PersistenceContext(unitName = "explanationsDbPU")
    private EntityManager entityManager;

    /**
     * Retrieves all explanations from the database, sorted by creation date in descending order.
     *
     * @return A list of {@link ExplanationEntity} objects representing all explanations.
     */
    public List<ExplanationEntity> getDataBaseData() {
        return entityManager.createQuery("SELECT e FROM ExplanationEntity e ORDER BY e.creationDate DESC", ExplanationEntity.class)
                .getResultList(); //EmptyList in case of no result
    }

    /**
     * Retrieves an explanation from the database based on its unique identifier.
     *
     * @param id The unique identifier of the {@link ExplanationEntity} to retrieve.
     * @return The {@link ExplanationEntity} object corresponding to the specified identifier.
     * @throws jakarta.persistence.NoResultException If no entity matches the specified identifier.
     */
    public ExplanationEntity getExplanationById(long id) throws NoResultException {
        return entityManager.createQuery("SELECT e FROM ExplanationEntity e WHERE e.id = :id", ExplanationEntity.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    /**
     * Persists a new {@link ExplanationEntity} in the database.
     * This operation is performed within a transaction using the {@link Transactional} annotation.
     *
     * @param explanation The {@link ExplanationEntity} to persist in the database.
     */
    @Transactional
    public void setDataBaseData(ExplanationEntity explanation) {
        entityManager.persist(explanation);
    }

    /**
     * Deletes an {@link ExplanationEntity} from the database based on its identifier.
     * Returns true if the entity was found and deleted, otherwise returns false.
     *
     * @param id The identifier of the {@link ExplanationEntity} to delete.
     * @return true if the entity was deleted, false if no entity with the specified identifier was found.
     */
    @Transactional
    public boolean deleteDataBaseData(int id) {
        var entity = entityManager.find(ExplanationEntity.class, id);
        if (entity == null) {
            return false;
        }
        entityManager.remove(entity);
        return true;
    }

}
