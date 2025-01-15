<template>
  <div class="llm-selector">
    <h3>Choisissez un modèle LLM :</h3>
    <select v-model="selectedModel" class="form-select">
      <option v-for="model in models" :key="model" :value="model">
        {{ model }}
      </option>
    </select>
    <button
      class="btn btn-primary mt-3 me-2"
      :disabled="isGenerating"
      @click="handleRequest"
    >
      {{ isGenerating ? "En cours..." : "Lancer" }}
    </button>
    <button class="btn btn-primary mt-3" @click="openModal">
      Voir la réponse
    </button>
    <div class="modal" v-if="isModalVisible" @click.self="closeModal">
      <div class="modal-dialog modal-dialog-centered custom-modal-size">
        <div class="modal-content">
          <span class="close" @click="closeModal">&times;</span>
          <div class="modal-header">
            <h5 class="modal-title">Réponse du LLM</h5>
          </div>
          <div class="modal-body">
            <div class="response">
              <p>{{ getResponse }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "LLMZone",
  props: {
    code: {
      type: String,
      required: true,
    },
    errors: {
      type: String,
      required: true,
    },
    llmResponse: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      selectedModel: "",
      models: ["LLAMA_3_2_1B", "MISTRAL_7_B_V0_2", "GEMMA2_2B"],
      formattedResponse: [],
      isModalVisible: false,
      isGenerating: false,
      ws: null, // WebSocket instance
    };
  },
  computed: {
    getResponse() {
      if (this.isGenerating || this.llmResponse === "") {
        return this.formattedResponse.join("");
      } else {
        return this.llmResponse;
      }
    },
  },
  methods: {
    handleRequest() {
      this.sendToLLM();
    },
    sendToLLM() {
      if (this.errors === "Compilation réussie !" || this.errors === "") {
        this.formattedResponse.push("No Error in compilation");
        return;
      }

      this.formattedResponse = [];
      this.isModalVisible = true;
      this.isGenerating = true;

      const payload = {
        code: this.code,
        errors: this.errors,
        selectedModel: this.selectedModel,
      };

      const url = `ws://localhost:8080/llm`;

      if (this.ws) {
        this.ws.close();
      }

      this.ws = new WebSocket(url);

      this.ws.onopen = () => {
        console.log("WebSocket connection established.");
        this.ws.send(JSON.stringify(payload));
      };

      this.ws.onmessage = (event) => {
        if (event.data === "done") {
          this.ws.close();
          return;
        }
        this.formattedResponse.push(event.data);
        this.formattedResponse = [this.formattedResponse.join("")];
      };

      this.ws.onerror = (error) => {
        console.error("WebSocket error:", error);
        this.ws.close();
      };

      this.ws.onclose = () => {
        this.isGenerating = false;
        console.log("WebSocket connection closed.");
      };
    },
    openModal() {
      this.isModalVisible = true;
    },
    closeModal() {
      this.isModalVisible = false;
    },
  },
};
</script>

<style scoped>
.custom-modal-size {
  width: 80vw;
  max-width: 90%;
  margin: 10px;
}
.modal {
  display: flex;
  position: fixed;
  z-index: 1;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0, 0, 0, 0.5);
}
.modal-body {
  max-height: 70vh;
  overflow-y: auto;
}
.response {
  white-space: pre-wrap;
  word-wrap: break-word;
}
.close {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
}
.close:hover,
.close:focus {
  color: black;
  text-decoration: none;
  cursor: pointer;
}
</style>
