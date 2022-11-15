package com.yunmo.iot.api.rule;

import com.yunmo.domain.common.*;
import com.yunmo.boot.web.ApiPageable;
import com.yunmo.iot.domain.rule.TimerAction;
import com.yunmo.iot.domain.rule.service.TimerActionSchedulerService;
import com.yunmo.iot.domain.rule.value.TimerActionSearch;
import com.yunmo.iot.domain.rule.value.TimerActionValue;
import com.yunmo.iot.repository.jpa.TimerActionJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me/timers")
@AllArgsConstructor
public class OwnerTimerActionResource {
    TimerActionJpaRepository timerActionRepository;
    
    TimerActionSchedulerService actionSchedulerService;


    @ApiPageable
    @GetMapping
    public Page<?> getList(@Principal Tenant tenant, TimerActionSearch search, Pageable paging) {
        search.setTenantId(tenant.getId());
        return timerActionRepository.findAll(Example.of(search.create()), paging);
    }

    @PostMapping
    public TimerAction create(@Principal Tenant tenant, @RequestBody TimerActionValue value) {
        TimerAction timerAction = value.create();
        timerAction.setTenantId(tenant.getId());
        timerAction.initVersion();
        return actionSchedulerService.schedule(timerAction);
    }

    @GetMapping("/{id}")
    public TimerAction getById(@Principal Tenant tenant, @PathVariable("id") TimerAction timerAction) {
        Asserts.found(timerAction, r->r.getTenantId().equals(tenant.getId()));
        return timerAction;
    }

    @DeleteMapping("/{id}")
    public void deleteByID(@Principal Tenant tenant, @PathVariable("id") TimerAction timerAction) {
        Asserts.found(timerAction, r->r.getTenantId().equals(tenant.getId()));
        timerActionRepository.delete(timerAction);
    }

    @PutMapping("/{id}")
    public TimerAction update(@Principal Tenant tenant, @PathVariable("id") TimerAction timerAction,
                           @RequestBody TimerActionValue value) {
        Asserts.found(timerAction, r->r.getTenantId().equals(tenant.getId()));
        value.assignTo(timerAction);
        timerAction.refreshVersion();
        return actionSchedulerService.schedule(timerAction);
    }

    @PatchMapping("/{id}")
    public TimerAction patch(@Principal Tenant tenant, @PathVariable("id") TimerAction timerAction,
                              @RequestBody TimerActionValue value) {
        Asserts.found(timerAction, r->r.getTenantId().equals(tenant.getId()));
        value.patchTo(timerAction);
        timerAction.refreshVersion();
        return timerActionRepository.save(actionSchedulerService.schedule(timerAction));
    }
}
