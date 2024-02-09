package ru.practicum.service.category;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(Pageable pageable);

    CategoryDto getCategory(long catId);

    CategoryDto createCategory(NewCategoryDto dto);

    void deleteCategory(long catId);

    CategoryDto updateCategory(long catId, CategoryDto dto);
}
