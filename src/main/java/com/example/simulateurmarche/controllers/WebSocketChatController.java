package com.example.simulateurmarche.controllers;


import com.example.simulateurmarche.entities.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/chat")
public class WebSocketChatController {


@PostMapping("/puttt")
        @MessageMapping("/sendMessage")
        @SendTo("/topic/public")
        public ChatMessage SendMessage (

                @Payload ChatMessage chatMessage
        )
        {
                return chatMessage ;
        }

        @MessageMapping("/addUser")
        @SendTo("/topic/public")
        public ChatMessage addUser(

                @Payload ChatMessage chatMessage ,
                SimpMessageHeaderAccessor headerAccessor

        ){
                 // add username in websocket session
                headerAccessor.getSessionAttributes().put("username",chatMessage.getSender());
                return chatMessage ;

        }

}