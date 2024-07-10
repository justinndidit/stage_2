package com.surgee.stage_2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class AppConfig {

    @Bean
    public Dotenv Dotenv() {
        return Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();
    }

}
