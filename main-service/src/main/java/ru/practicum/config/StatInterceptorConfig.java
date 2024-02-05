package ru.practicum.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.practicum.client.StatClient;
import ru.practicum.controller.publicAPI.StatInterceptor;

import java.util.Set;
import java.util.regex.Pattern;

@Configuration
public class StatInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private StatClient statsClient;

    @Bean
    StatInterceptor statsInterceptor() {
        Set<Pattern> uris = Set.of(
                Pattern.compile("^/events$"),
                Pattern.compile("^/events/\\d+$")
        );
        return new StatInterceptor(uris, statsClient);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(statsInterceptor());
    }
}
