import { createRouter, createWebHashHistory } from "vue-router";
import HomePage from "./components/HomePage.vue";
import Compilation from "./components/Compilation.vue";

const routes = [
  {
    path: "/",
    name: "home",
    component: HomePage,
  },

  {
    path: "/compile/:id?",
    name: "compile",
    component: Compilation,
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

export default router;
