package fr.uge.xplain.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tjake.jlama.model.AbstractModel;
import com.github.tjake.jlama.safetensors.prompt.PromptContext;
import fr.uge.xplain.ExplanationEntity;
import fr.uge.xplain.llms.Model;
import fr.uge.xplain.services.DataBaseService;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * WebSocket server endpoint for managing interactions with the LLM.
 * It supports receiving messages, processing them with the specified model,
 * and storing the generated explanations in the database.
 */
@ServerEndpoint("/llm")
public final class LLMResource {

    private record Payload(String code, String errors, String selectedModel) {
        private Payload {
            Objects.requireNonNull(code);
            Objects.requireNonNull(errors);
        }
    }

    private final DataBaseService dataBaseService;

    /**
     * Constructor for dependency injection of the database service.
     *
     * @param dataBaseService the service used to interact with the database
     */
    @Inject
    public LLMResource(DataBaseService dataBaseService) {
        this.dataBaseService = dataBaseService;
    }

    /**
     * Handles a new client connection.
     *
     * @param session the WebSocket session for the connected client
     */
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Client connected: " + session.getId());
    }

    /**
     * Processes a message from the client to generate explanations for errors in the provided source code.
     *
     * @param session the WebSocket session of the client
     * @param message the message containing the source code, errors, and model identifier
     * @throws IOException if an error occurs while sending a response
     */
    @OnMessage
    public void explainErrors(Session session, String message) throws IOException {
        var objectMapper = new ObjectMapper();
        var payload = objectMapper.readValue(message, Payload.class);
        var sourceCode = payload.code();
        var errors = payload.errors();
        var selectedModel = payload.selectedModel();

        var abstractModel = resolveModel(selectedModel, session).loadModel();
        var promptContext = Model.getContext(abstractModel, sourceCode, errors);
        var explanations = generateExplanations(abstractModel, promptContext, session);

        setDataInDataBase(selectedModel, sourceCode, explanations, dataBaseService);
        session.getAsyncRemote().sendText("done");
    }

    /**
     * Handles the disconnection of a client.
     *
     * @param session the WebSocket session of the disconnected client
     */
    @OnClose
    public void onClose(Session session) {
        System.out.println("Client disconnected: " + session.getId());
    }

    private Model resolveModel(String modelName, Session session) {
        final var DEFAULT_MODEL = Model.GEMMA2_2B;
        if (modelName == null) {
            session.getAsyncRemote().sendText("Selected model is null. Using default: " + DEFAULT_MODEL.name() + "\n");
            return DEFAULT_MODEL;
        }
        try {
            return Model.valueOf(modelName);
        } catch (IllegalArgumentException _) {
            session.getAsyncRemote().sendText("Unknown model: " + modelName + ". Using default: " + DEFAULT_MODEL.name() + "\n");
            return DEFAULT_MODEL;
        }
    }

    private String generateExplanations(AbstractModel abstractModel, PromptContext ctx, Session session) {
        var explanations = abstractModel.generate(UUID.randomUUID(), ctx, 0.0f, 1024, (s, _) -> session.getAsyncRemote().sendText(s));
        return explanations.responseText;
    }

    private void setDataInDataBase(String selectedModel, String sourceCode, String explanations, DataBaseService dataBaseService) {
        var explanation = new ExplanationEntity();
        explanation.setSourceCode(sourceCode);
        explanation.setExplanations(explanations);
        explanation.setCreationDate(LocalDate.now());
        explanation.setLlmModel(selectedModel);
        dataBaseService.setDataBaseData(explanation);
    }

}