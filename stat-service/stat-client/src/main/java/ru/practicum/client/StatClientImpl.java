package ru.practicum.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.CreatingStatDto;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatRequest;
import ru.practicum.exception.StatServiceException;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatClientImpl implements StatClient {
    private final RestTemplate rest;
    private final String basicUrl;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatClientImpl(@Value("${stat-server.url}") String basicUrl, RestTemplateBuilder builder) {
        this.rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(basicUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
        this.basicUrl = basicUrl;
    }

    @Override
    public void postHit(CreatingStatDto dto) {
        log.info("Отправка запроса на сервер статистики с записью нового обращения к Url = {}", dto.getUri());
        ResponseEntity<Object> response = makeAndSendRequest(HttpMethod.POST,
                basicUrl + "/hit",
                null,
                dto,
                new ParameterizedTypeReference<>() {
                });
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new StatServiceException("Ошибка при регистрации обращения к Url " + dto.getUri() + ". Код ошибки: " +
                    response.getStatusCode());
        }
        log.info("Запись добавлена успешно");

    }

    @Override
    public ResponseEntity<List<StatDto>> get(StatRequest request) {
        log.info("Отправка запроса на получение статистики с параметрами start = {}, end = {}, uris = {}, unique = {}",
                request.getStart().format(FORMATTER),
                request.getEnd().format(FORMATTER),
                request.getUris(),
                request.getUnique());
        StringBuilder sb = new StringBuilder("/stats?start={start}&end={end}&unique={unique}");
        Map<String, Object> params = new HashMap<>();
        params.put("start", request.getStart().format(FORMATTER));
        params.put("end", request.getEnd().format(FORMATTER));
        params.put("unique", request.getUnique());
        if (request.getUris() != null) {
            sb.append("&uris={uris[]}");
            params.put("uris[]", request.getUris().toArray());
        }

        ResponseEntity<List<StatDto>> response = makeAndSendRequest(HttpMethod.GET,
                basicUrl + sb,
                params,
                null,
                new ParameterizedTypeReference<>() {});
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new StatServiceException("Ошибка при получении списка статистики. Код ошибки: " +
                    response.getStatusCode());
        }
        return response;
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(
            HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body,
            ParameterizedTypeReference<R> respType) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<R> serverResponse;
        try {
            if (parameters != null) {
                serverResponse = rest.exchange(path, method, requestEntity, respType, parameters);
            } else {
                serverResponse = rest.exchange(path, method, requestEntity, respType);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (ResourceAccessException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        }
        return prepareClientResponse(serverResponse);
    }

    private <R> ResponseEntity<R> prepareClientResponse(ResponseEntity<R> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(null);
        }

        return responseBuilder.build();
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
