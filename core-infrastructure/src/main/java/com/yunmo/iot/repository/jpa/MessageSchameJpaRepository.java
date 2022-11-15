package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.core.MessageSchema;
import com.yunmo.iot.domain.core.repository.MessageSchemaRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageSchameJpaRepository extends JpaRepository<MessageSchema,Long>, MessageSchemaRepository {
    @Cacheable(cacheNames = "MessageSchema", key = "#p0")
    Optional<MessageSchema> findById(Long id);

    @CacheEvict(cacheNames = { "MessageSchema" }, key = "#p0.id")
    void delete(MessageSchema entity);

    @CacheEvict(cacheNames = { "MessageSchema" }, key = "#p0")
    void deleteById(Long id);

    @CachePut(cacheNames = { "MessageSchema" }, key = "#p0.id")
    <S extends MessageSchema> S save(S entity);
}
