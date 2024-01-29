package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.entity.Category;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        return repository.findAll(pageable)
                .map(CategoryMapper.INSTANCE::categoryToCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategory(long catId) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        return CategoryMapper.INSTANCE.categoryToCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto dto) {
        Category category = CategoryMapper.INSTANCE.newCategoryDtoToCategory(dto);
        return CategoryMapper.INSTANCE.categoryToCategoryDto(repository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(long catId) {
        repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        repository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long catId, CategoryDto dto) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        category.setName(dto.getName());
        return CategoryMapper.INSTANCE.categoryToCategoryDto(repository.save(category));
    }
}
