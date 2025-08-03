package com.votreprojet.livepoll;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import io.javalin.http.Context;
import io.javalin.websocket.WsContext;

import io.javalin.websocket.WsConfig;

public class PollController {
    private static final Map<String, Poll> polls = new ConcurrentHashMap<>();
    private static final Map<String, java.util.Set<WsContext>> pollListeners = new ConcurrentHashMap<>();

    // Méthode pour créer un sondage
    public static void create(Context ctx) {
        String question = ctx.formParam("question");
        List<String> options = ctx.formParams("options").stream()
                                  .filter(opt -> opt != null && !opt.isBlank())
                                  .collect(Collectors.toList());

        if (question == null || question.isBlank() || options.size() < 2) {
            ctx.status(400).result("Question et au moins 2 options sont requises.");
            return;
        }

        String id = UUID.randomUUID().toString().substring(0, 6);
        Poll newPoll = new Poll(id, question, options);
        polls.put(id, newPoll);
        ctx.redirect("/results.html?id=" + id);
    }

    // Méthode pour récupérer les données d'un sondage (utilisé par la page de vote)
    public static void getPollData(Context ctx) {
        String pollId = ctx.pathParam("id");
        Poll poll = polls.get(pollId);
        if (poll != null) {
            ctx.json(poll);
        } else {
            ctx.status(404).result("Sondage non trouvé");
        }
    }

    // Gère la connexion WebSocket pour les résultats
   public static void handleResultsWebSocket(WsConfig ws) {
        ws.onConnect(ctx -> {
            String pollId = ctx.pathParam("id");
            System.out.println("Nouveau spectateur pour le sondage: " + pollId);
            // On ajoute le contexte de la nouvelle connexion à la liste des auditeurs
            pollListeners.computeIfAbsent(pollId, k -> new java.util.HashSet<>()).add(ctx);
            // On envoie immédiatement l'état actuel des votes à ce nouvel auditeur
            Poll poll = polls.get(pollId);
            if (poll != null) {
                ctx.send(poll); // Envoi uniquement à la personne qui vient de se connecter
            }
        });

        ws.onClose(ctx -> {
            String pollId = ctx.pathParam("id");
            System.out.println("Un spectateur a quitté le sondage: " + pollId);
            // On retire le contexte de la connexion fermée
            if (pollListeners.containsKey(pollId)) {
                pollListeners.get(pollId).remove(ctx);
            }
        });

        // La section ws.onMessage est bien retirée car les votes ne passent plus par ici.
    }

    // Méthode pour envoyer les résultats à tous les spectateurs d'un sondage
    private static void broadcastPollResults(String pollId) {
        Poll poll = polls.get(pollId);
        if (poll != null) {
            java.util.Set<WsContext> listeners = pollListeners.get(pollId);
            if (listeners != null) {
                listeners.forEach(listener -> listener.send(poll));
            }
        }
    }

    // Méthode pour voter sur un sondage
    public static void voteOnPoll(Context ctx) {
        String pollId = ctx.pathParam("id");
        String option = ctx.formParam("option");
        
        Poll poll = polls.get(pollId);
        if (poll == null || option == null) {
            ctx.status(400).result("Sondage ou option invalide.");
            return;
        }
        String cookieName = "voted-" + pollId;
        if (ctx.cookie(cookieName) != null) {
            ctx.status(403).result("Vous avez déjà voté sur ce sondage.");
            return;
        }
        poll.addVote(option);
        System.out.println("Vote pour '" + option + "' dans " + pollId + " accepté de " + ctx.ip());
        ctx.cookie(cookieName, "true", 365 * 24 * 60 * 60);
        broadcastPollResults(pollId);
        ctx.status(200).result("Vote enregistré !");
    }


}