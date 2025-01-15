package com.example.fishmail.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class DynamicTaskService {
    
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledTask;

    public void scheduleTask(LocalDateTime dateTime, Runnable task){

        // Anuluj poprzednie zadanie, jeśli istnieje
        if(scheduledTask != null){
            scheduledTask.cancel(false);
        }

        // Przekształć LocalDateTime na Date
        Date startTime = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        
        // Zaplanuj nowe zadanie
        scheduledTask = taskScheduler.schedule(task, startTime);
    }
    
}
