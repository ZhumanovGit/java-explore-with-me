package ru.practicum.controller.adminAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.service.category.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCategoryController {
    private final CategoryService service;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategory(@Valid @RequestBody NewCategoryDto dto) {
        log.info("Обработка запроса на создание новой категории");
        CategoryDto result = service.createCategory(dto);
        log.info("Создана новая категория с id={}", result.getId());
        return result;
    }

    @DeleteMapping(path = "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(name = "catId") long catId) {
        log.info("Обработка запроса на удаление категории с id = {}", catId);
        service.deleteCategory(catId);
        log.info("Категория с id = {} удалена", catId);
    }

    @PatchMapping(path = "/{catId}")
    public CategoryDto patchCategory(@PathVariable(name = "catId") long catId,
                                     @Valid @RequestBody CategoryDto categoryUpdates) {
        log.info("Обработка запроса на обновление категории c id = {}", catId);
        CategoryDto result = service.updateCategory(catId, categoryUpdates);
        log.info("Категория с id = {} обновлена", result.getId());
        return result;
    }
}
