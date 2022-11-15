package com.yunmo.iot.repository.jpa;

import com.yunmo.iot.domain.core.DeviceConnectionStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.yunmo.iot.domain.core.Device;
import com.yunmo.iot.domain.core.repository.DeviceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;

public interface DeviceJpaRepository extends JpaRepository<Device, Long>, DeviceRepository {

    Page<Device> findByProjectIdAndIdNotInAndGateway(Long hubId, List<Long> childrenIds, boolean isGateway, Pageable paging);
    Page<Device> findByProjectIdAndGateway(Long hubId, boolean isGateway, Pageable paging);

    @Query("select distinct productId from Device where projectId = ?1 and deleted = false")
    List<Long> findProductByProjectId(Long id);

    @Modifying
    @Query("update Device set deviceStatus = 'ONLINE', lastOnlineTime = ?2 where id in ?1 and (lastOfflineTime < ?2 or lastOfflineTime is null)")
    void online(List<Long> ids, Instant time);


    @Modifying
    @Query("update Device set deviceStatus = 'OFFLINE', lastOfflineTime = ?2 where id in ?1 and (lastOnlineTime < ?2 or lastOnlineTime is null )")
    void offline(List<Long> ids, Instant time);

    @Modifying
    @Query("update Device set deviceStatus = ?2 where id in ?1")
    void setOnlineStatus(List<Long> ids, DeviceConnectionStatus status);

    List<Device> findByIdOrLocalId(Long id, String localId);

    List<Device> findByProjectIdAndIdOrProjectIdAndLocalId(Long projectId, Long id, Long projectId2, String localId);

    Page<Device> findByProjectIdIn(List<Long> projectIds, Pageable paging);

    @Query(value = "select * from device where json_extract(config,concat('$.content.',?1))=?2",nativeQuery = true)
    List<Device> findByConfigContentProperty(String key,String value);

    @Query(value = "select * from device where json_extract(attributes,concat('$.',?1))=?2",nativeQuery = true)
    List<Device> findByAttributesProperty(String key,String value);

}
