package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.service.RecordCacheService;
import com.testframe.autotest.core.enums.SceneExecuteEnum;
import com.testframe.autotest.core.enums.SceneStatusEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.meta.vo.common.PageVO;
import com.testframe.autotest.core.repository.*;
import com.testframe.autotest.domain.record.RecordDomain;
import com.testframe.autotest.domain.scene.SceneDomain;
import com.testframe.autotest.meta.dto.record.SceneSimpleExecuteDto;
import com.testframe.autotest.meta.vo.SceneSimpleInfo;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.scene.SceneSearchListDto;
import com.testframe.autotest.meta.query.RecordQry;
import com.testframe.autotest.meta.query.SceneQry;
import com.testframe.autotest.meta.vo.SceneListVO;
import com.testframe.autotest.service.SceneListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @date:2022/10/29 20:33
 * @author: lyf
 */
@Slf4j
@Service
public class SceneListImpl implements SceneListService {

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Autowired
    private CategorySceneRepository categorySceneRepository;

    @Autowired
    private SceneDomain sceneDomain;

    @Autowired
    private RecordDomain recordDomain;

    @Autowired
    private RecordCacheService recordCacheService;

    @Autowired
    private StepOrderRepository stepOrderRepository;

    @Autowired
    private SceneExecuteRecordRepository sceneExecuteRecordRepository;


    @Override
    public SceneListVO queryScenes(SceneQry sceneQry) {
        log.info("[SceneListImpl:queryScenes] queryScenes param = {}", JSON.toJSONString(sceneQry));
        try {
            PageQry pageQry = sceneQry.getPageQry();
            PageVO pageVO = new PageVO();
            pageVO.setPageNum(pageQry.getPage());
            pageVO.setPageSize(pageQry.getSize());
            Long offset = Long.valueOf((pageQry.getPage()-1)*pageQry.getSize());
            pageQry.setSize(pageQry.getSize()+1); // 多找一个
            pageQry.setOffset(offset);
            SceneListVO sceneListVO = new SceneListVO();

            SceneSearchListDto sceneSearchListDto = sceneDomain.searchScene(sceneQry);
            List<SceneDetailDto> sceneDetailDtos = sceneSearchListDto.getSceneDetailDtos();
            if (sceneDetailDtos == null || sceneDetailDtos.isEmpty()) {
                sceneListVO.setScenes(Collections.EMPTY_LIST);
                pageVO.setHasNext(false);
                pageVO.setLastId(-1L);
                pageVO.setTotalCount(0L);
                pageVO.setTotalPage(0);
                return sceneListVO;
            }

            List<Long> sceneIds = sceneDetailDtos.stream().map(sceneDetailDto -> sceneDetailDto.getSceneId())
                    .collect(Collectors.toList());
            if (sceneIds.size() == pageQry.getSize()) { // 多找了一个出来
                log.info("[SceneListImpl:queryScenes] queryScenes find more");
                pageVO.setHasNext(true);
                pageVO.setLastId(sceneIds.get(sceneIds.size()-2));
                sceneDetailDtos.remove(pageQry.getSize()-1);
            } else {
                pageVO.setHasNext(false);
                pageVO.setLastId(sceneIds.get(sceneIds.size()-1));
            }
            pageVO.setTotalCount(sceneSearchListDto.getTotal());
            pageVO.setTotalPage((int) (sceneSearchListDto.getTotal() / pageVO.getPageSize()
                    + (sceneSearchListDto.getTotal() % pageVO.getPageSize() == 0 ? 0 : 1)));

            // 批量获取场景执行记录
            // 仅需要最新的一条记录
            RecordQry recordQry = new RecordQry();
            recordQry.setPage(1);
            recordQry.setSize(1);
            recordQry.setType(SceneExecuteEnum.SINGLE.getType()); // 仅查询单独场景的执行
            CompletableFuture<HashMap<Long, SceneSimpleExecuteDto>> sceneExeRecordsFuture = CompletableFuture.supplyAsync(()
                    -> recordCacheService.RecSceneSimpleExeRecFromCache(sceneIds));

            return CompletableFuture.allOf(sceneExeRecordsFuture).thenApply(e -> {
                HashMap<Long, SceneSimpleExecuteDto> sceneSimpleExeRecords = sceneExeRecordsFuture.join();
                // 组装相关Vo
                List<SceneSimpleInfo> sceneSimpleInfoDtos = sceneDetailDtos.stream().map(sceneDetailDto -> {
                    SceneSimpleInfo sceneSimpleInfo = new SceneSimpleInfo();
                    sceneSimpleInfo.setSceneId(sceneDetailDto.getSceneId());
                    sceneSimpleInfo.setSceneName(sceneDetailDto.getSceneName());
                    sceneSimpleInfo.setStepNum(sceneDetailDto.getStepNum());
                    SceneSimpleExecuteDto sceneSimpleExecuteDto = sceneSimpleExeRecords.get(sceneDetailDto.getSceneId());
                    this.buildSceneRunStatus(sceneSimpleInfo, sceneSimpleExecuteDto);
                    return sceneSimpleInfo;
                }).collect(Collectors.toList());
                sceneListVO.setScenes(sceneSimpleInfoDtos);
                sceneListVO.setPageVO(pageVO);
                return sceneListVO;
            }).join();
        } catch (Exception e) {
            log.error("[SceneListImpl:queryScenes] query scene list error, reason = {}", e);
            throw new AutoTestException("场景列表查询错误");
        }
    }

    private void buildSceneRunStatus(SceneSimpleInfo sceneSimpleInfo, SceneSimpleExecuteDto sceneSimpleExecuteDto) {
        if (sceneSimpleExecuteDto == null) {
            // 无执行记录，丛未执行过
            sceneSimpleInfo.setStatus(SceneStatusEnum.NEVER.getType());
            sceneSimpleInfo.setExecuteTime(0L);
        } else {
            sceneSimpleInfo.setStatus(sceneSimpleExecuteDto.getStatus());
            sceneSimpleInfo.setExecuteTime(sceneSimpleExecuteDto.getExecuteTime());
        }
    }

}
