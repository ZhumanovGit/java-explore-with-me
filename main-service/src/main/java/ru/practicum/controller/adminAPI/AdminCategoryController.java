package ru.practicum.controller.adminAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCategoryController {


    @PostMapping
    public CategoryDto postCategory(@Valid @RequestBody NewCategoryDto dto) {
        return null;
    }

    @DeleteMapping(path = "/{catId}")
    public void deleteCategory(@PathVariable(name = "catId") Long catId) {

    }

    @PatchMapping(path = "{catId}")
    public CategoryDto patchCategory(@Valid @RequestBody CategoryDto categoryUpdates) {
        return null;
    }
}
