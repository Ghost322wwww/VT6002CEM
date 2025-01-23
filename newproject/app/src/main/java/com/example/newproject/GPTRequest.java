package com.example.newproject;

public class GPTRequest {
    private String model;
    private String prompt;
    private int max_tokens;

    public GPTRequest(String prompt) {
        this.model = "gpt-3.5-turbo";
        this.prompt = prompt;
        this.max_tokens = 150;
    }
}
