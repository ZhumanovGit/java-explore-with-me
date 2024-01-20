package ru.practicum.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
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
                dto);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new StatServiceException("Ошибка при регистрации обращения к Url " + dto.getUri() + ". Код ошибки: " +
                    response.getStatusCode());
        }
        log.info("Запись добавлена успешно");

    }

    @Override
    public List<StatDto> get(StatRequest request) {
        log.info("Отправка запроса на получение статистики с параметрами {}", request.toString());
        Map<String, Object> params = new HashMap<>();
        params.put("start", request.getStart().format(FORMATTER));
        params.put("end", request.getEnd().format(FORMATTER));
        params.put("uris", request.getUris());
        params.put("unique", request.getUnique());

        ResponseEntity<Object> response = makeAndSendRequest(HttpMethod.GET,
                basicUrl + "/stats?start={start}&end={end}&&uris={uris}&&unique={unique}",
                params,
                null);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new StatServiceException("Ошибка при получении списка статистики. Код ошибки: " +
                    response.getStatusCode());
        }

        ObjectMapper mapper = new ObjectMapper();
        List<StatDto> result = mapper.convertValue(response.getBody(),
                mapper.getTypeFactory().constructCollectionType(List.class, StatDto.class));
        log.info("Сервер вернул список длинной {}", result.size());
        return result;
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                      String path,
                                                      @Nullable Map<String, Object> parameters,
                                                      @Nullable CreatingStatDto body) {
        HttpEntity<CreatingStatDto> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> statServerResponse;
        try {
            if (parameters != null) {
                statServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        log.info("Ответ получен, код ответа: {}", statServerResponse.getStatusCode());
        return statServerResponse;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
