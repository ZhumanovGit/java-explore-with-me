package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.entity.Category;
import ru.practicum.entity.Event;
import ru.practicum.exception.model.DataConnectivityException;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        return repository.findAll(pageable)
                .map(categoryMapper::categoryToCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategory(long catId) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        return categoryMapper.categoryToCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto dto) {
        Category category = categoryMapper.newCategoryDtoToCategory(dto);
        return categoryMapper.categoryToCategoryDto(repository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(long catId) {
        repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        List<Event> events = eventRepository.findByCategoryId(catId);
        if (!events.isEmpty()) {
            throw new DataConnectivityException("Category with id=" + catId + " has events");
        }
        repository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long catId, CategoryDto dto) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        category.setName(dto.getName());
        return categoryMapper.categoryToCategoryDto(repository.save(category));
    }
}
