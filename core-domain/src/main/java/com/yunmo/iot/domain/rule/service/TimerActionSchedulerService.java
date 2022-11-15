package com.yunmo.iot.domain.rule.service;

import com.yunmo.domain.common.Events;
import com.yunmo.domain.common.TopicMessage;
import com.yunmo.iot.domain.rule.ActionRepeat;
import com.yunmo.iot.domain.rule.TimerAction;
import com.yunmo.iot.domain.rule.repository.CommandTemplateRepository;
import com.yunmo.iot.domain.rule.repository.TimerActionRepository;
import com.yunmo.iot.pipe.core.Topics;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Slf4j
@Service
public class TimerActionSchedulerService {
    @Autowired
    TimerActionRepository timerActionRepository;

    @Autowired
    CommandTemplateRepository commandTemplateRepository;

    @Data
    @Accessors(chain = true)
    public static class ActionSchedule {
        TimerAction timerAction;
    }

    private Instant scheduleTime(TimerAction timerAction) {
        ZonedDateTime scheduleTime = timerAction.getTriggerTime()
                .atZone(ZoneOffset.ofHours(timerAction.getTimeZoneOffsetHours()));
        Instant earliest = Instant.now().plus(10, ChronoUnit.SECONDS);
        while (scheduleTime.toInstant().isBefore(earliest)) {
            switch (timerAction.getActionRepeat()) {
                case EVERY_HOUR:
                    scheduleTime = scheduleTime.plusHours(1);
                    break;
                case ONCE:
                case EVERY_DAY:
                    scheduleTime = scheduleTime.plus(Period.ofDays(1));
                    break;
                case EVERY_WEEK:
                    scheduleTime = scheduleTime.plus(Period.ofWeeks(1));
                    break;
                case EVERY_MONTH:
                    scheduleTime = scheduleTime.plus(Period.ofMonths(1));
                    break;
                case WORK_DAY:
                    do {
                        scheduleTime = scheduleTime.plus(Period.ofDays(1));
                    } while (scheduleTime.getDayOfWeek() == DayOfWeek.SATURDAY
                            || scheduleTime.getDayOfWeek() == DayOfWeek.SUNDAY);
                    break;
                case CUSTOM_DAYS_OF_WEEK:
                    do {
                        scheduleTime = scheduleTime.plus(Period.ofDays(1));
                    } while (!timerAction.getCustomDays().contains(scheduleTime.getDayOfWeek()));
                    break;
            }
        }

        return scheduleTime.toInstant();
    }

    public TimerAction schedule(TimerAction timerAction) {
        timerAction.setTriggerTime(scheduleTime(timerAction));
        timerActionRepository.save(timerAction);
        enqueue(timerAction);
        return timerAction;
    }

    private void enqueue(TimerAction timerAction) {
        ActionSchedule schedule = new ActionSchedule()
                .setTimerAction(timerAction);
        if (timerAction.getEnabled()) {
            Events.post(schedule);
        }
    }

    @EventListener
    public void doAndReschedule(TimerAction action) {
        timerActionRepository.findById(action.getId()).ifPresentOrElse(a -> {
            if (a.getVersion().equals(action.getVersion()) && a.getEnabled()) {
                doAction(action);
                if(!action.getActionRepeat().equals(ActionRepeat.ONCE)) {
                    action.setTriggerTime(scheduleTime(action));
                    enqueue(action);
                }
            }
        }, ()->log.warn("timer action:{} not found!", action.getId()));
    }

    private void doAction(TimerAction action) {
        commandTemplateRepository.findById(action.getCommandTemplateId()).ifPresentOrElse(commandTemplate -> {
            Events.post(new TopicMessage()
                    .setTopic(Topics.deviceCommand(action.getTargetDeviceId(), commandTemplate.getChannel()).toString())
                    .setContent(Base64.getDecoder().decode(commandTemplate.getCommandEncoded())));
        }, ()->log.warn("command template:{} not found!", action.getCommandTemplateId()));
    }
}
