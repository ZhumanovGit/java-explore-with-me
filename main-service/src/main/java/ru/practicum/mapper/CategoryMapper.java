package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.config.IgnoreUnmappedMapperConfig;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.entity.Category;

@Mapper(config = IgnoreUnmappedMapperConfig.class)
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category newCategoryDtoToCategory(NewCategoryDto dto);

    CategoryDto categoryToCategoryDto(Category category);
}
