package com.example.simulateurmarche.controllers;

import com.example.simulateurmarche.services.chatService;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
public class TextGenerationController {


    @Autowired
    private chatService huggingFaceService;
    @PostMapping("/generate-text")
    @ResponseBody
    public ResponseEntity<String> generateText(@RequestBody String userMessage) {
        String response = huggingFaceService.generateText("respond me as an expert finance , give me  the response in one text in french, question: "+userMessage);
        return ResponseEntity.ok(response);
    }



}
