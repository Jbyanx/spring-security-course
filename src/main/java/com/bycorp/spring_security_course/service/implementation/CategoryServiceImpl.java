package com.bycorp.spring_security_course.service.implementation;

import com.bycorp.spring_security_course.dto.request.SaveCategory;
import com.bycorp.spring_security_course.dto.response.GetCategory;
import com.bycorp.spring_security_course.exception.CategoryNotFoundException;
import com.bycorp.spring_security_course.mapper.CategoryMapper;
import com.bycorp.spring_security_course.persistence.entity.Category;
import com.bycorp.spring_security_course.persistence.repository.CategoryRepository;
import com.bycorp.spring_security_course.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @PreAuthorize("hasAuthority('READ_ALL_CATEGORIES')")
    @Override
    public Page<GetCategory> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toGetCategory);
    }

    @PreAuthorize("hasAuthority('READ_ONE_CATEGORY')")
    @Override
    public GetCategory getById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toGetCategory)
                .orElseThrow(() -> new CategoryNotFoundException("category not found"));
    }

    @PreAuthorize("hasAuthority('CREATE_ONE_CATEGORY')")
    @Override
    public GetCategory save(SaveCategory saveCategory) {
        Category category = new Category();
        category.setName(saveCategory.name());
        category.setStatus(Category.CategoryStatus.ENABLED);

        return categoryMapper.toGetCategory(categoryRepository.save(category));
    }

    @PreAuthorize("hasAuthority('UPDATE_ONE_CATEGORY')")
    @Override
    public GetCategory updateById(SaveCategory saveCategory, Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("category not found"));
        /*update*/
        category.setName(saveCategory.name());
        return categoryMapper.toGetCategory(categoryRepository.save(category));
    }

    @PreAuthorize("hasAuthority('DISABLE_ONE_CATEGORY')")
    @Override
    public GetCategory disableById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("category not found"));
        /*disable*/
        category.setStatus(Category.CategoryStatus.DISABLED);
        return categoryMapper.toGetCategory(categoryRepository.save(category));
    }
}

