<template>
  <div class="container">
    <!-- Titre et bouton retour -->
    <div class="d-flex justify-content-start align-items-center mb-4 w-100">
      <router-link to="/">
        <button class="btn btn-primary me-3">Retour</button>
      </router-link>
      <h1 class="mx-auto">Java Code Compiler</h1>
    </div>

    <div class="row">
      <!-- Section Code et Compilation -->
      <div class="col-md-6">
        <div class="form-group">
          <label for="codeArea">Code:</label>
          <textarea
            id="codeArea"
            class="form-control code-editor"
            rows="10"
            v-model="code"
            @keydown="handleTabKey"
            placeholder="Écrivez votre code ici..."
          ></textarea>
        </div>
        <div class="text-center my-3">
          <button class="btn btn-primary w-100 py-2" @click="compileCode">
            Compiler
          </button>
        </div>
        <div class="alert" :class="resultClass" v-if="compileResult">
          <p><strong>Résultat de la compilation :</strong></p>
          <pre>{{ compileResult }}</pre>
        </div>
      </div>

      <!-- Section LLM -->
      <div class="col-md-6">
        <LLMZone
          :code="code"
          :errors="compileResult"
          :llmResponse="llmResponse"
        />
      </div>
    </div>
  </div>
</template>

<script>
import LLMZone from "./LLMZone.vue";

export default {
  name: "Compilation",
  components: { LLMZone },
  data() {
    return {
      code: "",
      compileResult: "",
      llmResponse: "",
      explanationId: null,
    };
  },
  mounted() {
    if (this.$route.params.id) {
      this.explanationId = this.$route.params.id;
      this.fetchExplanation();
    }
  },
  computed: {
    resultClass() {
      return this.compileResult.includes("ERROR")
        ? "alert-danger"
        : "alert-success";
    },
  },
  methods: {
    handleTabKey(event) {
      if (event.key === "Tab") {
        event.preventDefault();
        const textarea = event.target;
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        this.code =
          this.code.substring(0, start) + "\t" + this.code.substring(end);
        this.$nextTick(() => {
          textarea.selectionStart = textarea.selectionEnd = start + 1;
        });
      }
    },
    fetchExplanation() {
      const url = `http://localhost:8080/database/${this.explanationId}`;
      fetch(url)
        .then((response) => {
          if (!response.ok) throw new Error("Network response was not ok");
          return response.json();
        })
        .then((data) => {
          this.code = data.sourceCode;
          this.llmResponse = data.explanations;
        })
        .catch((error) => {
          console.error(
            "Erreur lors de la récupération de l'explication :",
            error
          );
          alert("Impossible de charger les données de l'explication.");
        });
    },
    compileCode() {
      fetch("http://localhost:8080/compile", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ code: this.code }),
      })
        .then((response) => {
          if (!response.ok) {
            return response.json().then((errorData) => {
              this.compileResult =
                errorData.response?.join("\n") || "ERROR: Une erreur inconnue s'est produite.";
            });
          }
          return response.json().then((data) => {
            this.compileResult = data.message;
          });
        })
        .catch(() => {
          this.compileResult = "ERROR: Erreur de connexion au serveur.";
        });
    },
  },
};
</script>

<style scoped>
.code-editor {
  font-family: "Courier New", Courier, monospace;
  font-size: 14px;
  background-color: #f4f4f4;
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 10px;
  white-space: pre-wrap;
  color: #333;
  resize: vertical;
}

.code-editor:focus {
  border-color: #0056b3;
  box-shadow: 0 0 5px rgba(0, 86, 179, 0.5);
}

pre {
  white-space: pre-wrap;
  word-wrap: break-word;
}

</style>
