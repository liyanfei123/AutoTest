package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.enums.SceneStatusEnum;
import com.testframe.autotest.core.enums.StepRunResultEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneExecuteRecordRepository;
import com.testframe.autotest.core.repository.StepExecuteRecordRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.meta.bo.SceneExecuteRecord;
import com.testframe.autotest.meta.bo.SceneStepOrder;
import com.testframe.autotest.meta.bo.StepExecuteRecord;
import com.testframe.autotest.meta.dto.execute.SceneExeRecordDto;
import com.testframe.autotest.meta.dto.execute.StepExeRecordDto;
import com.testframe.autotest.meta.vo.SceneRecordListVo;
import com.testframe.autotest.service.SceneRecordService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @date:2022/11/05 23:04
 * @author: lyf
 */
@Slf4j
@Service
public class SceneRecordServiceImpl implements SceneRecordService {

    @Autowired
    private SceneExecuteRecordRepository sceneExecuteRecordRepository;

    @Autowired
    private StepExecuteRecordRepository stepExecuteRecordRepository;

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private StepOrderRepository stepOrderRepository;


    @Override
    public SceneRecordListVo records(Long sceneId) {
        try {
            log.info("[SceneDetailImpl:query] query execute records in sceneId {}", sceneId);
            SceneRecordListVo sceneRecordListVo = new SceneRecordListVo();
            // 查询当前场景下的执行记录
            HashMap<Long, SceneExecuteRecord> sceneExecuteRecordMap = sceneExecuteRecordRepository.querySceneExecuteRecordBySceneId(sceneId);
            Set<Long> recordIdset = sceneExecuteRecordMap.keySet();
            if (recordIdset.isEmpty()) {
                // 当前场景从未执行过
                sceneRecordListVo.setSceneId(sceneId);
                sceneRecordListVo.setSceneExeRecordDtos(Collections.EMPTY_LIST);
                return sceneRecordListVo;
            }
            // 执行中的数据返回


            List<Long> recordIds = new ArrayList<>(recordIdset);
            // 批量获取场景执行下的多个步骤执行顺序
            CompletableFuture<HashMap<Long, List<StepExecuteRecord>>> stepExecuteRecordsFuture = CompletableFuture
                    .supplyAsync(() -> batchGetStepExeInfo(recordIds));
            // 批量获取场景执行下的各个步骤的执行信息
            CompletableFuture<HashMap<Long, SceneStepOrder>> stepOrderFuture = CompletableFuture
                    .supplyAsync(() -> batchGetStepOrder(recordIds));

            return CompletableFuture.allOf(stepExecuteRecordsFuture, stepOrderFuture).thenApply( e -> {
                HashMap<Long, List<StepExecuteRecord>> stepExecuteRecords = stepExecuteRecordsFuture.join();
                HashMap<Long, SceneStepOrder> stepOrders = stepOrderFuture.join();
                // 组装VO
                sceneRecordListVo.setSceneId(sceneId);
                toSceneDetailInfo(sceneRecordListVo, sceneExecuteRecordMap, stepExecuteRecords, stepOrders);
                return sceneRecordListVo;
            }).join();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[SceneDetailImpl:query] query execute records {} error, reason: ", sceneId, e);
            throw new AutoTestException("查询执行记录失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveRecord(Long sceneId, List<Long> orderList, Integer status) {
        try {
            Scene scene = sceneDetailRepository.querySceneById(sceneId);
            Long currentTime = System.currentTimeMillis();
            SceneExecuteRecord sceneExecuteRecord = SceneExecuteRecord.build(scene);
            sceneExecuteRecord.setExecuteTime(currentTime);
            sceneExecuteRecord.setStepOrderList(orderList);
            sceneExecuteRecord.setStatus(status);
            log.info("[SceneRecordServiceImpl:saveRecord] save step execute record, sceneExecuteRecord = {}",
                    JSON.toJSONString(sceneExecuteRecord));
            return sceneExecuteRecordRepository.saveSceneExecuteRecord(sceneExecuteRecord);
        } catch (Exception e) {
            log.error("[SceneRecordServiceImpl:saveRecord] save step execute record, sceneId {} error {}", sceneId, e);
            throw new AutoTestException("保存场景执行记录失败");
        }
    }


    /**
     *
     * @param recordId 执行记录id
     * @param status 执行状态
     * @param extInfo 执行错误信息
     * @return
     */
    @Override
    public Boolean updateRecord(Long recordId, Integer status, String extInfo) {
        try {
            SceneExecuteRecord sceneExecuteRecord = sceneExecuteRecordRepository.getSceneExeRecordById(recordId);
            sceneExecuteRecord.setStatus(status);
            sceneExecuteRecord.setExtInfo(extInfo);
            return sceneExecuteRecordRepository.updateSceneExecuteRecord(recordId, sceneExecuteRecord);
        } catch (Exception e) {
            log.error("[SceneRecordServiceImpl:updateRecord] update scene execute record, recordId {} error {}", recordId, e);
            throw new AutoTestException("更新场景执行记录失败");
        }


    }

    private HashMap<Long, List<StepExecuteRecord>> batchGetStepExeInfo(List<Long> recordIds) {
        HashMap<Long, List<StepExecuteRecord>> stepExecuteRecordMap = stepExecuteRecordRepository.batchQueryStepExeRecord(recordIds);
        log.info("[SceneRecordServiceImpl:batchGetStepExeInfo] get step execute records: {}", JSON.toJSONString(stepExecuteRecordMap));
        return stepExecuteRecordMap;

    }

    private HashMap<Long, SceneStepOrder> batchGetStepOrder(List<Long> recordIds) {
        HashMap<Long, SceneStepOrder> sceneStepOrderMap = stepOrderRepository.bacthQueryStepExeOrderByRecordIds(recordIds);
        sceneStepOrderMap.forEach((recordId, sceneStepOrder) -> {
            List<Long> orderList = sceneStepOrder.getOrderList();
            sceneStepOrder.setOrderList(orderList);
        });
        log.info("[SceneRecordServiceImpl:batchGetStepOrder] get step order, datas = {}", JSON.toJSONString(sceneStepOrderMap));
        return sceneStepOrderMap;
    }

    private void toSceneDetailInfo(SceneRecordListVo sceneRecordListVo,
                                   HashMap<Long, SceneExecuteRecord> sceneExecuteRecordMap,
                                   HashMap<Long, List<StepExecuteRecord>> stepExecuteRecordMap,
                                   HashMap<Long, SceneStepOrder> stepOrders) {
        // 对场景步骤顺序重排
        stepExecuteRecordMap.forEach((recordId, stepExecuteRecords) -> {
            List<Long> stepOrderList = stepOrders.get(recordId).getOrderList(); // 执行时的顺序
            // 使用匿名比较器排序
            Collections.sort(stepExecuteRecords, new Comparator<StepExecuteRecord>() {
                @Override
                public int compare(StepExecuteRecord record1, StepExecuteRecord record2) {
                    return stepOrderList.indexOf(record1.getStepId()) - stepOrderList.indexOf(record2.getStepId());
                }
            });
        });
        List<SceneExeRecordDto> sceneExeRecordDtos = new ArrayList<>(sceneExecuteRecordMap.size());
        sceneExecuteRecordMap.forEach((recordId, sceneExecuteRecord) -> {
            List<StepExecuteRecord> stepExecuteRecords = stepExecuteRecordMap.get(recordId);
            SceneExeRecordDto sceneExeRecordDto = new SceneExeRecordDto();
            SceneExeRecordDto.build(sceneExeRecordDto, sceneExecuteRecord);
            // 根据步骤的执行状态来确定当前场景的执行结果
            sceneExeRecordDto.setStatus(checkStatus(stepExecuteRecords));
            // 将步骤执行信息转化为dto
            List<StepExeRecordDto> stepExeRecordDtos = stepExecuteRecords.stream().map(StepExeRecordDto::build)
                    .collect(Collectors.toList());
            sceneExeRecordDto.setStepExeRecordDtos(stepExeRecordDtos);
            sceneExeRecordDtos.add(sceneExeRecordDto);
        });
        sceneRecordListVo.setSceneExeRecordDtos(sceneExeRecordDtos);
    }


    private Integer checkStatus(List<StepExecuteRecord> stepExecuteRecords) {
        List<Integer> status = stepExecuteRecords.stream().map(StepExecuteRecord::getStatus)
                .collect(Collectors.toList());
        if (status.isEmpty()) {
            return SceneStatusEnum.NEVER.getType();
        } else if (status.contains(StepRunResultEnum.RUN.getType())) {
            return SceneStatusEnum.ING.getType();
        } else if (status.contains(StepRunResultEnum.FAIL.getType())) {
            return SceneStatusEnum.FAIL.getType();
        }
        return SceneStatusEnum.SUCCESS.getType();
    }


}
