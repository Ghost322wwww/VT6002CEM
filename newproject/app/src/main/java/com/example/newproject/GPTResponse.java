package com.example.newproject;

public class GPTResponse {
    private Choice[] choices;

    public String getText() {
        return choices != null && choices.length > 0 ? choices[0].text : "";
    }

    public static class Choice {
        public String text;
    }
}
