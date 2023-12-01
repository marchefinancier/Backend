package com.example.simulateurmarche.controllers;

import com.example.simulateurmarche.entities.SmsRequest;
import com.example.simulateurmarche.services.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/sms")
public class SmsController {


    private final TwilioService twilioService;

    @Autowired
    public SmsController(TwilioService twilioService) {
        this.twilioService = twilioService;
    }

    @PostMapping
    public void sendSms(@Valid @RequestBody SmsRequest smsRequest) {
        twilioService.sendSms(smsRequest);
    }
}
