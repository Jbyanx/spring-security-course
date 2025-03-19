package com.bycorp.spring_security_course.persistence.repository;

import com.bycorp.spring_security_course.persistence.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> getProductByName(String name);
    Page<Product> findAll(Pageable pageable);
}
