# Doc Utilisateur Projet Xplain

Projet Réalisé par :

- CHERRATI SidAli

## Prérequis

Il faut que **npm** soit installé dans la machine qui compile le projet

## Execution du projet

Il faut d'abord compiler le projet via Maven, puis utiliser le fichier `run_jar.sh` pour lancer le JAR avec les options
nécessaires.

## Prise en main / Utilisation

L'API est composée de deux fenêtres. La première, intitulée **'Home'**, affiche l'historique des discussions. Un bouton
est disponible pour passer à la fenêtre suivante **'Compiler de nouveau'**. Cette dernière permet d'écrire du code, de
le compiler, de voir le résultat de la compilation et de demander une explication en cas d'erreurs.

Vous pouvez sélectionner le LLM que vous souhaitez utiliser :

- **LLAMA_3_2_1B** (le modèle léger)
- **MISTRAL_7_B_V0_2** (le modèle lourd)
- **GEMMA2_2B** (le modèle moyen)

Si aucun modèle n'est sélectionné, le modèle moyen (**GEMMA2_2B**) sera utilisé par défaut.
