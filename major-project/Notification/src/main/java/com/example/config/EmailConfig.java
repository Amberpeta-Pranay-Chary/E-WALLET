package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Bean
    SimpleMailMessage getMailMessage(){
        return new SimpleMailMessage();
    }
    @Bean
    JavaMailSender getMailSender()
    {
        JavaMailSenderImpl javaMailSender=new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("e.wallet.jbdl50.majorproject@gmail.com");
        javaMailSender.setPassword("yzztfjdbpedljkvt");
        //Now we want to add the debug logs of this JavaMailSender so , we can make that true with the properties.
        Properties properties=javaMailSender.getJavaMailProperties(); //Getting already configured properties
        properties.put("mail.debug",true); //making true
        properties.put("mail.smtp.starttls.enable",true); //we should make this true so that we can send mail from the above mentioned mail account
        return javaMailSender;
    }
}
