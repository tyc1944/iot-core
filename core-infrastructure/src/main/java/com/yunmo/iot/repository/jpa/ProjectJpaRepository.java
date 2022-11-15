package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.core.Project;
import com.yunmo.iot.domain.core.repository.ProjectRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectJpaRepository extends JpaRepository<Project, Long> , ProjectRepository {

    @Cacheable(cacheNames = "Projects", key = "#p0")
    Optional<Project> findById(Long id);

    @CacheEvict(cacheNames = { "Projects" }, key = "#p0.id")
    void delete(Project entity);

    @CacheEvict(cacheNames = { "Projects" }, key = "#p0")
    void deleteById(Long id);

    @CachePut(cacheNames = { "Projects" }, key = "#p0.id")
    <S extends Project> S save(S entity);

    Page<Project> findByTenantId(Long id, Pageable paging);
    List<Project> findByTenantId(Long id);

    List<Project> findByOperatorId(long operatorId);
}
