# Live Poll - Système de Sondage en Temps Réel

Une application web full-stack construite avec Java, Javalin et WebSockets. Ce projet démontre la création d'une application interactive sans base de données, avec un accent sur la légèreté et la communication en temps réel.

## Fonctionnalités
- 🚀 Création de sondages dynamiques.
- 📊 Affichage des résultats mis à jour instantanément pour tous les utilisateurs.
- 🍪 Vote unique par navigateur grâce aux cookies HTTP.
- 🎨 Interface soignée construite avec TailwindCSS et notifications "toast".
- ☕ Entièrement construit en Java, servi par un serveur web embarqué Javalin.

## Technologies Utilisées
*   **Backend:** Java 17, Javalin, Maven
*   **Temps réel:** WebSockets
*   **Frontend:** HTML5, TailwindCSS, JavaScript (ES6+)

## Comment lancer le projet
1.  Clonez ce dépôt.
2.  Assurez-vous d'avoir un JDK 17+ et Maven installés.
3.  Exécutez la commande à la racine du projet :
    ```bash
    mvn compile exec:java
    ```
4.  Ouvrez votre navigateur à l'adresse `http://localhost:7070`.