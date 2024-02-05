package ru.practicum.controller.publicAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.practicum.client.StatClient;
import ru.practicum.dto.CreatingStatDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Slf4j
public class StatInterceptor implements HandlerInterceptor {

    private final Set<Pattern> uris;

    private final StatClient client;

    private boolean isObserved(final String uri) {
        return uris.stream()
                .anyMatch(pattern -> pattern.matcher(uri).matches());
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) throws Exception {

        if (response.getStatus() >= 200 && response.getStatus() < 300) {
            String requestURI = request.getRequestURI();
            if (isObserved(requestURI)) {
                 client.postHit( new CreatingStatDto("main-service",
                                 requestURI,
                                 request.getRemoteAddr(),
                                 LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            }
        }
    }
}
