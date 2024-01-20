package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.CreatingStatDto;
import ru.practicum.dto.StatDto;
import ru.practicum.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatController.class)
class StatControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    StatService service;
    @Autowired
    private MockMvc mvc;

    @Test
    public void saveStat_whenRequestIsCorrect_thenReturnStatus200() throws Exception {
        CreatingStatDto dto = CreatingStatDto.builder()
                .app("testApp")
                .uri("testUri")
                .ip("testIp")
                .timestamp("testDate")
                .build();
        mvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void saveStat_whenRequestHasWrongBody_thenReturnStatus400() throws Exception {
        CreatingStatDto dto = CreatingStatDto.builder()
                .app("testApp")
                .ip("testIp")
                .timestamp("testDate")
                .build();
        mvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getStat_whenRequestIsCorrectWithNoUriAndUniqueIsFalse_thenReturnListOfStats() throws Exception {
        StatDto first = StatDto.builder().uri("test").hits(1L).build();
        StatDto second = StatDto.builder().uri("test2").hits(1L).build();
        StatDto third = StatDto.builder().uri("test3").hits(1L).build();
        List<StatDto> result = List.of(first, second, third);
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2021, 1, 1, 1, 1, 1);
        when(service.getStats(start, end, null, false)).thenReturn(result);

        mvc.perform(get("/stats")
                        .param("start", "2020-01-01 01:01:01")
                        .param("end", "2021-01-01 01:01:01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void getStat_whenRequestIsCorrectWithNoUrisAndUniqueIsTrue_thenReturnListOfStats() throws Exception {
        StatDto first = StatDto.builder().uri("test").hits(2L).build();
        StatDto third = StatDto.builder().uri("test3").hits(1L).build();
        List<StatDto> result = List.of(first, third);
        LocalDateTime start = LocalDateTime.of(2020, 1, 1, 1, 1, 1);
        LocalDateTime end = LocalDateTime.of(2021, 1, 1, 1, 1, 1);
        when(service.getStats(start, end, null, true)).thenReturn(result);

        mvc.perform(get("/stats")
                        .param("start", "2020-01-01 01:01:01")
                        .param("end", "2021-01-01 01:01:01")
                        .param("unique", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void getStat_whenRequestIsWrong_thenReturnStatus400() throws Exception {
        mvc.perform(get("/stats")
                        .param("start", "2020-01-01 01:01:01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}