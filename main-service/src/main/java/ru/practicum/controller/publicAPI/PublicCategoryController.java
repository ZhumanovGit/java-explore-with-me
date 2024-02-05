package ru.practicum.controller.publicAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.category.CategoryService;

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryController {
    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(required = false, defaultValue = "0") int from,
                                           @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Обработка запроса на получение всех категорий с параметрами from = {}, size = {}", from, size);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<CategoryDto> result = service.getCategories(pageRequest);
        log.info("Получен список категорий длиной {}", result.size());
        return result;
    }

    @GetMapping(path = "/{catId}")
    public CategoryDto getCategory(@PathVariable(name = "catId") long catId) {
        log.info("Обработка запроса на получение категории с id = {}", catId);
        CategoryDto result = service.getCategory(catId);
        log.info("Получена категория с id = {}", result.getId());
        return result;
    }


}
