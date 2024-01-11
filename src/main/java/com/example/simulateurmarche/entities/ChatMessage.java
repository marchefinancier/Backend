package com.example.simulateurmarche.entities;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    private  String content ;

    private String sender ;

    private MessageType type ;


}
