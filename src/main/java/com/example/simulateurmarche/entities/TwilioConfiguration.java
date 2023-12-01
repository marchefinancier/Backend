package com.example.simulateurmarche.entities;

import com.twilio.http.TwilioRestClient;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class TwilioConfiguration {

    @Value("AC19ec6121833ffbcb460a8187f2e0bacf")
    private String accountSid;

    @Value("ba50931fe36d0aebdafcaefe1604c461")
    private String authToken;

    @Value("+16413819036")
    private String trialNumber;


    @Bean
    public TwilioRestClient twilioRestClient()
    {
        return new TwilioRestClient.Builder(accountSid, authToken).build();
    }


}