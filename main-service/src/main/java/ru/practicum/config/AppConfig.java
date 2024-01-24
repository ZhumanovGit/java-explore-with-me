package ru.practicum.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.client.StatClient;
import ru.practicum.client.StatClientImpl;

@Configuration
public class AppConfig {

    @Bean
    public StatClient statClient() {
        return new StatClientImpl("http://localhost:9090", new RestTemplateBuilder());
    }
}
