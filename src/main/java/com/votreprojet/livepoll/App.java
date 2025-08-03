package com.votreprojet.livepoll;

import io.javalin.Javalin;

public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
        }).start(7070);

        System.out.println("Serveur démarré sur http://localhost:7070");

        app.post("/create-poll", PollController::create);

        app.post("/api/poll/{id}/vote", PollController::voteOnPoll);

        app.get("/api/poll/{id}", PollController::getPollData);

        app.ws("/results-ws/{id}", PollController::handleResultsWebSocket);
    }
}