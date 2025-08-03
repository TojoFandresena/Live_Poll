package com.votreprojet.livepoll;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Poll {
    private final String id;
    private final String question;
    private final List<String> options;
    // Utiliser une Map pour stocker les votes de manière thread-safe
    private final ConcurrentHashMap<String, AtomicInteger> votes;

    public Poll(String id, String question, List<String> options) {
        this.id = id;
        this.question = question;
        this.options = options;
        this.votes = new ConcurrentHashMap<>();
        // Initialiser les votes pour chaque option à 0
        for (String option : options) {
            this.votes.put(option, new AtomicInteger(0));
        }
    }

    public void addVote(String option) {
        if (this.votes.containsKey(option)) {
            this.votes.get(option).incrementAndGet();
        }
    }

    // Getters pour que Javalin puisse les transformer en JSON
    public String getId() { return id; }
    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public ConcurrentHashMap<String, AtomicInteger> getVotes() { return votes; }
}