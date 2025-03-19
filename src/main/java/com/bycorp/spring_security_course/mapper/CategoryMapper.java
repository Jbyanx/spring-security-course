package com.bycorp.spring_security_course.mapper;

import com.bycorp.spring_security_course.dto.request.SaveCategory;
import com.bycorp.spring_security_course.dto.request.SaveProduct;
import com.bycorp.spring_security_course.dto.response.GetCategory;
import com.bycorp.spring_security_course.persistence.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(SaveCategory saveCategory);

    Category toCategory(GetCategory getCategory);

    GetCategory toGetCategory(Category category);

    List<GetCategory> toGetCategoryList(List<Category> categories);
}


