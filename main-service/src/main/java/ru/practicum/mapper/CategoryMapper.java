package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.config.IgnoreUnmappedMapperConfig;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.entity.Category;

@Mapper(config = IgnoreUnmappedMapperConfig.class, componentModel = "spring")
public interface CategoryMapper {

    Category newCategoryDtoToCategory(NewCategoryDto dto);

    CategoryDto categoryToCategoryDto(Category category);
}
