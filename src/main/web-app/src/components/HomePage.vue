<template>
  <div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1>Explications</h1>
      <router-link to="/compile">
        <button class="btn btn-primary">Compiler de nouveau</button>
      </router-link>
    </div>

    <!-- Search Input -->
    <div class="mb-4">
      <input
        v-model="searchQuery"
        type="text"
        class="form-control"
        placeholder="Recherche par nom de classe"
      />
    </div>

    <!-- Grille des explications -->
    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
      <div
        v-for="explanation in filteredExplanations"
        :key="explanation.id"
        class="col"
      >
        <div class="card h-100 shadow-sm">
          <button
            class="btn-close position-absolute top-0 end-0 m-2"
            aria-label="Delete"
            @click="deleteExplanation(explanation.id)"
          ></button>
          <!-- Temporary -->
          <div class="card-body">
            <h5 class="card-title">
              <!-- Afficher seulement les 3 premières lignes de sourceCode -->
              <span v-if="!explanation.expanded">{{
                truncatedLines(explanation.sourceCode)
              }}</span>
              <span v-else>{{ explanation.sourceCode }}</span>
            </h5>
            <small class="text-muted">{{
              new Date(explanation.creationDate).toLocaleDateString()
            }}</small>
            <small class="text-muted date-llm">{{
              explanation.llmModel
            }}</small>

            <p v-if="!explanation.expanded" class="card-text">
              {{ truncatedText(explanation.explanations) }}
            </p>
            <p v-else class="card-text">
              {{ explanation.explanations }}
            </p>
            <div class="btn-group">
              <button
                class="btn btn-sm btn-outline-primary"
                @click="toggleExplanation(explanation)"
              >
                {{ explanation.expanded ? "Voir moins" : "Voir plus" }}
              </button>
              <button
                class="btn btn-sm btn-outline-primary"
                @click="redirectToCompilation(explanation)"
              >
                {{ "Aller vers" }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "HomePage",
  data() {
    return {
      explanations: [],
      searchQuery: "",
    };
  },
  computed: {
    filteredExplanations() {
      if (!this.searchQuery) {
        return this.explanations;
      }
      const query = this.searchQuery.toLowerCase();
      return this.explanations.filter((explanation) =>
        explanation.sourceCode.toLowerCase().includes(query)
      );
    },
  },
  mounted() {
    this.fetchExplanations();
  },
  methods: {
    fetchExplanations() {
      fetch("http://localhost:8080/database")
        .then((response) => {
          if (!response.ok) {
            throw new Error("Network response was not ok");
          }
          return response.json();
        })
        .then((data) => {
          this.explanations = data.response.map((explanation) => ({
            ...explanation,
            expanded: false,
          }));
        })
        .catch((error) => {
          console.error("There was an error:", error);
        });
    },
    toggleExplanation(explanation) {
      explanation.expanded = !explanation.expanded;
    },
    truncatedText(text) {
      return text.length > 100 ? text.substring(0, 100) + "..." : text;
    },

    truncatedLines(text) {
      const lines = text.split("\n");
      if (lines.length <= 3) {
        return text;
      }
      return lines.slice(0, 3).join("\n") + "...";
    },

    redirectToCompilation(explanation) {
      const id = explanation.id;
      this.$router.push({ name: "compile", params: { id } });
    },

    deleteExplanation(id) {
      if (!confirm("Voulez-vous vraiment supprimer cette explication ?")) {
        // si il veut pas supprimer on sort pas d'execution du fetch
        return;
      }

      fetch(`http://localhost:8080/database/${id}`, {
        method: "DELETE",
      })
        .then((response) => {
          if (!response.ok) {
            alert("La suppression a échoué.");
            return;
          }
          this.explanations = this.explanations.filter(
            (explanation) => explanation.id !== id
          );
          alert("L'explication a été supprimée avec succès !");
        })
        .catch((error) => {
          console.error("Erreur lors de la suppression :", error);
          alert("Une erreur est survenue lors de la suppression.");
        });
    },
  },
};
</script>

<style scoped>
.card {
  border-radius: 8px;
}
.card-title {
  font-size: 1.2em;
  font-weight: bold;
  white-space: pre-line;
}
.btn-outline-primary {
  font-size: 0.9em;
}
.date-llm {
  margin-left: 8px;
}
</style>
