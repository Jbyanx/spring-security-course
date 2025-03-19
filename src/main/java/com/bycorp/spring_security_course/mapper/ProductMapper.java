package com.bycorp.spring_security_course.mapper;

import com.bycorp.spring_security_course.dto.request.SaveProduct;
import com.bycorp.spring_security_course.dto.response.GetProduct;
import com.bycorp.spring_security_course.persistence.entity.Category;
import com.bycorp.spring_security_course.persistence.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ProductMapper {

    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategory")
    Product toProduct(SaveProduct saveProduct);

    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategory")
    Product toProductFromSave(SaveProduct saveProduct);

    @Mapping(target = "category", source = "category.name")
    GetProduct toGetProduct(Product product);

    List<GetProduct> toGetProducts(List<Product> products);

    @Named("mapCategory")
    default Category mapCategory(Long categoryId) {
        if (categoryId == null) return null;
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
}



