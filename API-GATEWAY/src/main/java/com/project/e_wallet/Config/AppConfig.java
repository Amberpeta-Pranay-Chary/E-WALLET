package com.project.e_wallet.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AppConfig {
    @Bean
    public RestTemplate getTemplate()
    {
        return new RestTemplate();
    }
}
