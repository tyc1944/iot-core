package com.yunmo.iot.domain.core.repository;

import com.yunmo.domain.common.EntityRepository;
import com.yunmo.iot.domain.core.MessageSchema;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageSchemaRepository extends EntityRepository<MessageSchema, Long> {
    List<MessageSchema> findByProductId(Long productId);

    Optional<MessageSchema> findByProductIdAndChannel(Long productId, String channel);
    Optional<MessageSchema> findByChannelAndDeletedIsFalse(String channel);

    @Query(value = "select * from message_schema where id = :id", nativeQuery = true)
    Optional<MessageSchema> findDeletedAlsoById(@Param("id")Long id);

    @Modifying
    @Query("update MessageSchema  set deleted = true, overrideTime = now() where productId = :productId and channel = :channel and deleted = false")
    void override(@Param("productId") Long productId, @Param("channel")String channel);
}
