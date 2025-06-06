package com.bycorp.spring_security_course.controller;

import com.bycorp.spring_security_course.dto.request.SaveProduct;
import com.bycorp.spring_security_course.dto.response.GetProduct;
import com.bycorp.spring_security_course.persistence.util.RolePermission;
import com.bycorp.spring_security_course.service.ProductService;
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
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController( ProductService productService){
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Page<GetProduct>> getAllProducts(Pageable pageable){
        Page<GetProduct> productsPage = productService.getAll(pageable);
        if(productsPage.hasContent()){
            return ResponseEntity.ok(productsPage);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProduct> getOneProduct(@PathVariable Long id){
        GetProduct product = productService.getById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<GetProduct> saveOneProduct(@RequestBody @Valid SaveProduct product, HttpServletRequest request) {
        GetProduct productSaved = productService.save(product);

        URI newLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productSaved.id())
                .toUri();

        return ResponseEntity.created(newLocation).body(productSaved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetProduct> updateOneProduct(@RequestBody @Valid SaveProduct product,
                                                       @PathVariable Long id) {
        GetProduct productUpdated = productService.updateById(product, id);
        return ResponseEntity.ok(productUpdated);
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<GetProduct> disableOneProductById(@PathVariable Long id) {
        GetProduct productUpdated = productService.disableById(id);
        return ResponseEntity.ok(productUpdated);
    }
}
