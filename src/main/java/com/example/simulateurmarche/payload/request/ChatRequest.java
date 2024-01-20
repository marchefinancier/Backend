package com.example.simulateurmarche.payload.request;

public class ChatRequest {
    private String inputs;

    public ChatRequest(String inputs) {
        this.inputs = inputs;
    }

    public String getInputs() {
        return inputs;
    }

    public void setInputs(String inputs) {
        this.inputs = inputs;
    }
}