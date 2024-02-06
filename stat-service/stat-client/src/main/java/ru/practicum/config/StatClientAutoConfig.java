package ru.practicum.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.StatClient;
import ru.practicum.client.StatClientImpl;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(prefix = "stat-server", name = "url")
public class StatClientAutoConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestTemplate statsRestTemplate(@Value("${stat-server.url}") String serverUrl, RestTemplateBuilder builder) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .setConnectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public StatClient statClient(@Qualifier("statsRestTemplate") RestTemplate restTemplate) {
        return new StatClientImpl(restTemplate);
    }

}
