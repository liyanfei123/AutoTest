package com.testframe.autotest.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 定时任务配置
 */
@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfig implements SchedulingConfigurer {


    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        // 配置线程池
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);
        threadPoolTaskScheduler.setThreadNamePrefix("my-scheduled-task-pool-");
        threadPoolTaskScheduler.initialize();
        scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler);

//        // 监听cron表达式是否有修改
//        scheduledTaskRegistrar.addTriggerTask(() -> process(), triggerContext -> {
//                    String cron = "*/2 * * * * ?";
//                    log.info("进行自我监听中");
//                    if (cron == null || cron.isEmpty()) {
//                        log.info("cron is null");
//                    }
//                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
//                });
    }

    private void process() {
        log.info("基于接口定时任务");
    }
}
