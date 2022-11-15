package com.yunmo.iot.api.assets;


import com.yunmo.domain.common.Principal;
import com.yunmo.domain.common.Tenant;
import com.yunmo.iot.domain.core.EntityEventRecord;
import com.yunmo.iot.repository.cassandra.EntityEventRepository;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Transactional
@RestController
@RequestMapping("/api/entities/{entity}/events/{channel}")
public class EntityEventResource {
    @Autowired
    EntityEventRepository entityEventRepository;



    @GetMapping
    public List<EntityEventRecord> queryTelemetry(@Principal Tenant tenant,
                                                  @PathVariable String entity,
                                                  @PathVariable String channel,
                                                  @ApiParam("时间戳") @RequestParam long from,
                                                  @ApiParam("时间戳") @RequestParam(required = false) Optional<Long> to) {

        return entityEventRepository.findByTimeRange(entity, channel,
                Instant.ofEpochSecond(from), to.map(Instant::ofEpochSecond).orElse(Instant.now()), 3600);
    }

}
