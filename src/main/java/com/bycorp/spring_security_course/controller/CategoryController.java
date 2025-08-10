package com.bycorp.spring_security_course.controller;

import com.bycorp.spring_security_course.dto.request.SaveCategory;
import com.bycorp.spring_security_course.dto.response.GetCategory;
import com.bycorp.spring_security_course.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Page<GetCategory>> getAllCategories(Pageable pageable){
        Page<GetCategory> categoryPage = categoryService.getAll(pageable);
        if(categoryPage.hasContent()){
            return ResponseEntity.ok(categoryPage);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetCategory> getOneCategory(@PathVariable Long id){
        GetCategory category = categoryService.getById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<GetCategory> saveOneCategory(@RequestBody @Valid SaveCategory category, HttpServletRequest request) {
        GetCategory categorySaved = categoryService.save(category);

        URI newLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categorySaved.id())
                .toUri();

        return ResponseEntity.created(newLocation).body(categorySaved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetCategory> updateOneCategory(@RequestBody @Valid SaveCategory category,
                                                       @PathVariable Long id) {
        GetCategory categoryUpdated = categoryService.updateById(category, id);
        return ResponseEntity.ok(categoryUpdated);
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<GetCategory> disableOneCategoryById(@PathVariable Long id) {
        GetCategory category = categoryService.disableById(id);
        return ResponseEntity.ok(category);
    }
}
