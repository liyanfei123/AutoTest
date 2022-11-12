package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.enums.SceneStatusEnum;
import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.enums.StepStatusEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.meta.vo.common.PageVO;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneExecuteRecordRepository;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.meta.bo.SceneExecuteRecord;
import com.testframe.autotest.meta.bo.SceneStepOrder;
import com.testframe.autotest.meta.bo.SceneStepRel;
import com.testframe.autotest.meta.dto.SceneExecuteDto;
import com.testframe.autotest.meta.dto.SceneInfoDto;
import com.testframe.autotest.meta.dto.SceneSimpleInfoDto;
import com.testframe.autotest.meta.query.SceneQry;
import com.testframe.autotest.meta.vo.SceneListVO;
import com.testframe.autotest.service.SceneListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private StepOrderRepository stepOrderRepository;

    @Autowired
    private SceneExecuteRecordRepository sceneExecuteRecordRepository;


    @Override
    public SceneListVO queryScenes(SceneQry sceneQry) {
        try {
//        status 按状态列表来搜索
//        List<Integer> status = SceneStatusEnum.getTypes();
            PageQry pageQry = new PageQry();
            pageQry.setSize(sceneQry.getSize()+1);
            pageQry.setOffset(((sceneQry.getPage() - 1) * sceneQry.getSize()));
            pageQry.setLastId(sceneQry.getLastId());
            if (pageQry.getLastId() == null) {
                pageQry.setLastId(-1L);
            }

            SceneListVO sceneListVO = new SceneListVO();
            PageVO pageVO = new PageVO(sceneQry.getPage(), sceneQry.getSize());
            sceneListVO.setPageVO(pageVO);
            // 根据当前搜索条件搜索出的总数
            Long allCount = sceneDetailRepository.countScene(sceneQry.getSceneId(), sceneQry.getSceneName());
            sceneListVO.setTotal(allCount);
            int totalPage = (int) (allCount / sceneQry.getSize()) + ((allCount % sceneQry.getSize()) >= 1 ? 1 : 0);
            sceneListVO.setTotalPage(totalPage);

            // 获得size个场景
            List<Scene> scenes = sceneDetailRepository.queryScenes(sceneQry.getSceneId(), sceneQry.getSceneName(), pageQry);
            if (scenes.isEmpty()) {
                sceneListVO.setScenes(Collections.EMPTY_LIST);
                sceneListVO.setHasNext(false);
                sceneListVO.setLastId(-1L);
                return sceneListVO;
            }

            List<Long> sceneIds = scenes.stream().map(Scene::getId).collect(Collectors.toList());
            if (sceneIds.size() == pageQry.getSize()) { // 多找了一个出来
                sceneListVO.setHasNext(true);
                sceneListVO.setLastId(sceneIds.get(sceneIds.size()-2));

            } else {
                sceneListVO.setHasNext(false);
                sceneListVO.setLastId(sceneIds.get(sceneIds.size()-1));
            }

            // 批量获取场景执行记录
            CompletableFuture<HashMap<Long, SceneExecuteDto>> sceneExeRecordsFuture = CompletableFuture.supplyAsync(() -> batchGetSceneExeRecord(sceneIds));
            // 批量获取场景步骤数
            CompletableFuture<HashMap<Long, Integer>> sceneStepNumsFuture = CompletableFuture.supplyAsync(() -> batchGetSceneStepNum(sceneIds));

            List<SceneSimpleInfoDto> sceneSimpleInfoDtos = scenes.stream().map(scene -> {
                SceneSimpleInfoDto sceneSimpleInfoDto = new SceneSimpleInfoDto();
                sceneSimpleInfoDto.setId(scene.getId());
                sceneSimpleInfoDto.setSceneName(scene.getTitle());
                return sceneSimpleInfoDto;
            }).collect(Collectors.toList());

            return CompletableFuture.allOf(sceneExeRecordsFuture, sceneStepNumsFuture).thenApply(e -> {
                HashMap<Long, SceneExecuteDto> sceneExeRecords = sceneExeRecordsFuture.join();
                HashMap<Long, Integer> sceneStepNums = sceneStepNumsFuture.join();
                // 组装相关Vo
                sceneListVO.setScenes(sceneSimpleInfoDtos);
                toSceneListVO(sceneListVO, sceneExeRecords, sceneStepNums);
                return sceneListVO;
            }).join();
        } catch (Exception e) {
            log.error("[SceneListImpl:queryScenes] query scene list error, reason = {}", e);
            throw new AutoTestException("场景列表查询错误");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteScene(Long sceneId) {
        try {
            Scene scene = sceneDetailRepository.querySceneById(sceneId);
            if (scene == null) {
                throw new AutoTestException("当前场景不存在");
            }
            // 删除场景关联的步骤顺序
            List<SceneStepOrder> sceneStepOrders = stepOrderRepository.queryStepOrderBySceneId(sceneId);
            sceneStepOrders = sceneStepOrders.stream().filter(k -> k.getType() == StepOrderEnum.BEFORE.getType())
                    .collect(Collectors.toList());
            if (!sceneStepOrders.isEmpty()) {
                SceneStepOrder sceneStepOrder = sceneStepOrders.get(0);
                sceneStepOrder.setOrderList(null);
                stepOrderRepository.updateSceneStepOrder(sceneStepOrder);
            }
            // 删除场景关联的步骤
            List<SceneStepRel> sceneStepRels = sceneStepRepository.querySceneStepsBySceneId(sceneId);
            if (!sceneStepRels.isEmpty()) {
                sceneStepRels.forEach(sceneStepRel -> {
                    sceneStepRel.setIsDelete(1);
                    sceneStepRel.setStatus(StepStatusEnum.CLOSE.getType());
                });
                sceneStepRepository.batchUpdateSceneStep(sceneStepRels);
            }
            // 删除场景
            scene.setIsDelete(1);
            sceneDetailRepository.update(scene);
            return true;
        } catch (Exception e) {
            log.error("[SceneListInterImpl:deleteScene] delete scene {} error, reason = {}",
                    sceneId, e);
            throw new AutoTestException(e.getMessage());
        }
    }


    /**
     * 批量获取场景执行记录
     * @param sceneIds
     * @return
     */
    private HashMap<Long, SceneExecuteDto> batchGetSceneExeRecord(List<Long> sceneIds) {
        HashMap<Long, SceneExecuteDto> sceneExecuteDtoMap = new HashMap<>();
        PageQry pageQry = new PageQry(0, 1, -1L); // 仅需要最新的一条记录
        for (Long sceneId : sceneIds) {
            List<SceneExecuteRecord> sceneExecuteRecords = sceneExecuteRecordRepository.querySceneExecuteRecordBySceneId(sceneId, pageQry);
            if (sceneExecuteRecords.isEmpty()) {
                sceneExecuteDtoMap.put(sceneId, null);
            } else {
                sceneExecuteDtoMap.put(sceneId, SceneExecuteDto.toDto(sceneExecuteRecords.get(0)));
            }
        }
        log.info("[SceneListInterImpl:batchGetSceneExeRecord] sceneIds = {}, result = {}",
                JSON.toJSONString(sceneIds), JSON.toJSONString(sceneExecuteDtoMap));
        return sceneExecuteDtoMap;
    }

    /**
     * 批量获取场景步骤数
     * @param sceneIds
     * @return
     */
    private HashMap<Long, Integer> batchGetSceneStepNum(List<Long> sceneIds) {
        HashMap<Long, Integer> sceneStepNumMap = new HashMap<>();
        for (Long sceneId : sceneIds) {
            List<SceneStepRel> sceneStepRels = sceneStepRepository.querySceneStepsBySceneId(sceneId);
            sceneStepNumMap.put(sceneId, sceneStepRels.size());
        }
        log.info("[SceneListInterImpl:batchGetSceneStepNum] sceneIds = {}, result = {}",
                JSON.toJSONString(sceneIds), JSON.toJSONString(sceneStepNumMap));
        return sceneStepNumMap;
    }

    private void toSceneListVO(SceneListVO sceneListVO, HashMap<Long, SceneExecuteDto> sceneExeRecords, HashMap<Long, Integer> sceneStepNums) {
        List<SceneSimpleInfoDto> scenes = sceneListVO.getScenes();
        if (sceneListVO.getHasNext()) {
            scenes.remove(scenes.size()-1); // 由于存在下一页，故多了一个
        }
        for (SceneSimpleInfoDto sceneSimpleInfoDto : scenes) {
            Long sceneId = sceneSimpleInfoDto.getId();
            SceneExecuteDto sceneExecuteDto = sceneExeRecords.get(sceneId);
            sceneSimpleInfoDto.setStepNum(sceneStepNums.get(sceneId));
            if (sceneExecuteDto == null) {
                // 无执行记录，丛未执行过
                sceneSimpleInfoDto.setStatus(SceneStatusEnum.NEVER.getType());
                sceneSimpleInfoDto.setExecuteTime(0L);
            } else {
                sceneSimpleInfoDto.setStatus(sceneExecuteDto.getStatus());
                sceneExecuteDto.setExecuteTime(sceneExecuteDto.getExecuteTime());
            }
        }
    }




}
