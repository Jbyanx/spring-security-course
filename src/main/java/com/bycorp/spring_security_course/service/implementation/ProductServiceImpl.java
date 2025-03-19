package com.bycorp.spring_security_course.service.implementation;

import com.bycorp.spring_security_course.dto.request.SaveProduct;
import com.bycorp.spring_security_course.dto.response.GetProduct;
import com.bycorp.spring_security_course.exception.CategoryNotFoundException;
import com.bycorp.spring_security_course.exception.ProductNotFoundException;
import com.bycorp.spring_security_course.mapper.ProductMapper;
import com.bycorp.spring_security_course.persistence.entity.Category;
import com.bycorp.spring_security_course.persistence.entity.Product;
import com.bycorp.spring_security_course.persistence.repository.CategoryRepository;
import com.bycorp.spring_security_course.persistence.repository.ProductRepository;
import com.bycorp.spring_security_course.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    public GetProduct save(SaveProduct saveProduct) {
        // Buscar la categoría en la base de datos
        Category category = categoryRepository.findById(saveProduct.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        Product product = productMapper.toProduct(saveProduct);
        product.setStatus(Product.ProductStatus.ENABLED);
        product.setCategory(category); // Asignar categoría correctamente

        return productMapper.toGetProduct(productRepository.save(product));
    }

    @Override
    public GetProduct getById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toGetProduct)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    @Override
    public GetProduct getByName(String name) {
        return productRepository.getProductByName(name)
                .map(productMapper::toGetProduct)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    @Override
    public Page<GetProduct> getAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toGetProduct);
    }

    @Override
    @Transactional
    public GetProduct updateById(SaveProduct saveProduct, Long id) {
        Product productDb = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        updateProduct(productDb, saveProduct);
        return productMapper.toGetProduct(productRepository.save(productDb));
    }

    @Override
    public GetProduct disableById(Long id) {
        Product productDb = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productDb.setStatus(Product.ProductStatus.DISABLED);
        return productMapper.toGetProduct(productRepository.save(productDb));
    }

    private void updateProduct(Product productDb, SaveProduct saveProduct) {
        productDb.setName(saveProduct.name());
        productDb.setPrice(saveProduct.price());

        // Actualizar la categoría correctamente
        Category category = categoryRepository.findById(saveProduct.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        productDb.setCategory(category);
    }
}

