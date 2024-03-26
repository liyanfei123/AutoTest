package com.testframe.autotest.job;

import com.testframe.autotest.cache.ao.SceneDetailCache;
import com.testframe.autotest.core.meta.vo.common.Response;
import com.testframe.autotest.core.repository.dao.SceneDetailDao;
import com.testframe.autotest.job.base.ITaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 场景总数量缓存刷新
 */
@Slf4j
@EnableAsync // 开启多线程
@Component
public class SceneCountSchedule extends ITaskHandler {

    @Resource
    private SceneDetailDao sceneDetailDao;

    @Resource
    private SceneDetailCache sceneDetailCache;

    @Override
    @Async
//    @Scheduled(cron = "*/5 * * * * ?")
    public Response<String> execute() throws Exception {
        log.info("[SceneCountSchedule:execute] refresh scene total count start...");
        sceneDetailCache.clearSceneCountCache();
        Long count = sceneDetailDao.countScenes(null, null);
        sceneDetailCache.updateSceneCount(count);
        log.info("[SceneCountSchedule:execute] refresh scene total count end...");
        return Response.success();
    }



}
