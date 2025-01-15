package fr.uge.xplain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entity representing an explanation associated with a piece of source code.
 * This class is mapped to a database table to store information related
 * to explanations provided for a source code.
 *
 * <p>This entity contains a unique identifier, the source code, associated
 * explanations, and the creation date of the record.</p>
 */
@Entity
public class ExplanationEntity {

    /**
     * Unique identifier of the entity. This field is automatically generated
     * when the entity is persisted to the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    /**
     * The source code associated with the explanation.
     * This field is of type long text to store a potentially large piece of code.
     */
    @Column(name = "source_code", columnDefinition = "CLOB")
    private String sourceCode;

    /**
     * The detailed explanations for the source code.
     * This field is also of type long text, allowing for storing large and detailed explanations.
     */
    @Column(name = "explanations", columnDefinition = "CLOB")
    private String explanations;

    /**
     * The creation date of the entity. Represents the date the explanation was added to the system.
     */
    @Column(name = "creation_date")
    private LocalDate creationDate;

    /**
     * The model used to generate the explanations.
     */
    @Column(name = "llm_model")
    private String llmModel;

    /**
     * Default constructor required for JPA persistence.
     */
    public ExplanationEntity() {}

    /**
     * Returns the unique identifier of the entity.
     *
     * @return the unique identifier of the entity.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the source code associated with the explanation.
     *
     * @return the source code.
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * Returns the explanations associated with the source code.
     *
     * @return the explanations.
     */
    public String getExplanations() {
        return explanations;
    }

    /**
     * Returns the creation date of the entity.
     *
     * @return the creation date.
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }

    /**
     * Returns the model used for the explanation.
     *
     * @return the model, as a string.
     */
    public String getLlmModel() {
        return llmModel;
    }

    /**
     * Sets the unique identifier of the entity.
     *
     * @param id the identifier to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the source code for the entity.
     *
     * @param sourceCode the source code to set.
     */
    public void setSourceCode(String sourceCode) {
        Objects.requireNonNull(sourceCode);
        this.sourceCode = sourceCode;
    }

    /**
     * Sets the explanations associated with the source code.
     *
     * @param explanations the explanations to set.
     */
    public void setExplanations(String explanations) {
        Objects.requireNonNull(explanations);
        this.explanations = explanations;
    }

    /**
     * Sets the creation date of the entity.
     *
     * @param creationDate the creation date to set.
     */
    public void setCreationDate(LocalDate creationDate) {
        Objects.requireNonNull(creationDate);
        this.creationDate = creationDate;
    }

    /**
     * Sets the selected model of the entity.
     *
     * @param llmModel the model set.
     */
    public void setLlmModel(String llmModel) {
        Objects.requireNonNull(llmModel);
        this.llmModel = llmModel;
    }

}