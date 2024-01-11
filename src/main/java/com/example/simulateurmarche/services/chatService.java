package com.example.simulateurmarche.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class chatService {




        private final String apiUrl = "https://api-inference.huggingface.co/models/openchat/openchat-3.5-1210";
        private final String apiToken = "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";

    public String generateText(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        headers.set("Content-Type", "application/json");

        String requestBody = "{\"inputs\": \"" +userMessage+ "\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        // Log the entire response entity
        System.out.println("Response: " + response);

        if (response.getStatusCode() == HttpStatus.OK) {
            String output = response.getBody();
            System.out.println(output);
            return output;
        } else {
            return "Error occurred";
        }
    }


}
