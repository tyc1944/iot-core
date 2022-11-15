package com.yunmo.iot.api.service;

import com.google.common.base.Preconditions;
import com.yunmo.core.api.user.PersonnelService;
import com.yunmo.domain.common.Problems;
import com.yunmo.iot.domain.core.Project;
import com.yunmo.iot.domain.core.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/push")
@PreAuthorize("hasRole('INTERNAL')")
public class PushAuthService {

    @Autowired
    JwtDecoder jwtDecoder;

    @Autowired
    PersonnelService personnelService;

    @Autowired
    ProjectRepository projectRepository;

    static final Pattern EVENTS_TOPIC = Pattern.compile("projects/(\\d+)/devices/(\\d+|\\+)/events/?(\\w+)?");
    static final Pattern NOTIFICATION_TOPIC = Pattern.compile("notifications/(\\d+)/\\w+");

    @GetMapping("/auth")
    @PreAuthorize("hasRole('INTERNAL')")
    public void auth(@RequestParam String token,@RequestParam Long tenant) {
        long userTenantId = userTenantId(token);
        Preconditions.checkArgument(tenant.equals(userTenantId)
                || personnelService.findByEnterpriseIdAndUserId(tenant, userTenantId) != null);
    }

    @GetMapping("/acl")
    @PreAuthorize("hasRole('INTERNAL')")
    public void acl(@RequestParam String topic,@RequestParam Long tenant, @RequestParam int ps) {
        Preconditions.checkArgument(ps == 1);
        Matcher eventsMatcher = EVENTS_TOPIC.matcher(topic);
        if(eventsMatcher.matches()) {
            Project project = projectRepository.findById(Long.parseLong(eventsMatcher.group(1))).get();
            Preconditions.checkArgument(project.getTenantId().equals(tenant));
            return;
        }

        Matcher notifyMatcher = NOTIFICATION_TOPIC.matcher(topic);
        if(notifyMatcher.matches()) {
            long tenantId = Long.parseLong(notifyMatcher.group(1));
            Preconditions.checkArgument(tenant.equals(tenantId));
            return;
        }

        throw Problems.hint("no match");
    }


    private long userTenantId(String jwt) {
        Jwt token = jwtDecoder.decode(jwt);
        Preconditions.checkState(token.getExpiresAt() == null || token.getExpiresAt().isAfter(Instant.now()));
        return  ((Number)token.getClaim("id")).longValue();
    }
}
