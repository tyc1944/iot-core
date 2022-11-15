package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.core.Product;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    @Cacheable(cacheNames = "Product", key = "#p0")
    Optional<Product> findById(Long id);

    @CacheEvict(cacheNames = { "Product" }, key = "#p0.id")
    void delete(Product entity);

    @CacheEvict(cacheNames = { "Product" }, key = "#p0")
    void deleteById(Long id);

    @CachePut(cacheNames = { "Product" }, key = "#p0.id")
    <S extends Product> S save(S entity);

    Page<Product> findByTenantId(Long id, Pageable paging);
    List<Product> findByTenantId(Long id);

    Page<Product> findByIdIn(List<Long> productIds, Pageable paging);
}
