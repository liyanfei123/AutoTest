package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.config.AutoTestConfig;
import com.testframe.autotest.core.enums.SceneExecuteEnum;
import com.testframe.autotest.core.enums.SceneStatusEnum;
import com.testframe.autotest.core.enums.StepRunResultEnum;
import com.testframe.autotest.core.enums.StepTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneExecuteRecordRepository;
import com.testframe.autotest.core.repository.StepExecuteRecordRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.meta.bo.SceneExecuteRecord;
import com.testframe.autotest.meta.bo.SceneStepOrder;
import com.testframe.autotest.meta.bo.StepExecuteRecord;
import com.testframe.autotest.meta.dto.execute.*;
import com.testframe.autotest.meta.validator.SceneValidator;
import com.testframe.autotest.meta.vo.SceneRecordListVo;
import com.testframe.autotest.service.SceneRecordService;
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
 * @date:2022/11/05 23:04
 * @author: lyf
 */
@Slf4j
@Service
public class SceneRecordServiceImpl implements SceneRecordService {

    @Autowired
    private AutoTestConfig autoTestConfig;

    @Autowired
    private SceneValidator sceneValidator;

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
            if (!sceneValidator.sceneIsExist(sceneId)){
                throw new AutoTestException("请输入正确的场景id");
            }

            SceneRecordListVo sceneRecordListVo = new SceneRecordListVo();
            sceneRecordListVo.setSceneId(sceneId);
            // 查询当前场景下的执行记录 作为子场景执行的记录不查询
            PageQry pageQry = new PageQry(0, autoTestConfig.getRecordSize(), -1L, SceneExecuteEnum.SINGLE.getType());
            List<SceneExecuteRecord> sceneExecuteRecords = sceneExecuteRecordRepository.querySceneExecuteRecordBySceneId(sceneId, pageQry);
            if (sceneExecuteRecords.isEmpty()) {
                // 当前场景从未执行过
                log.info("[SceneDetailImpl:query] scene {} no execute records", sceneId);
                sceneRecordListVo.setSceneExeRecordDtos(Collections.EMPTY_LIST);
                return sceneRecordListVo;
            }

            HashMap<Long, SceneExecuteRecord> sceneExecuteRecordMap = new HashMap<>();
            sceneExecuteRecords.forEach(sceneExecuteRecord -> {
                sceneExecuteRecordMap.put(sceneExecuteRecord.getRecordId(), sceneExecuteRecord);
            });
            List<Long> recordIds = sceneExecuteRecords.stream().map(SceneExecuteRecord::getRecordId)
                    .collect(Collectors.toList());

            // 步骤执行顺序
            HashMap<Long, List<Long>> stepOrders = new HashMap<>();
            recordIds.forEach(recordId -> {
                stepOrders.put(recordId,
                        sceneExecuteRecordMap.get(recordId).getStepOrderList());
            } );

            // 批量获取场景执行下的多个步骤执行顺序
            // 对于子场景下的执行记录，同一个stepId下，会有多条记录
            CompletableFuture<HashMap<Long, List<StepExecuteRecord>>> stepExecuteRecordsFuture = CompletableFuture
                    .supplyAsync(() -> batchGetStepExeInfo(recordIds));

            return CompletableFuture.allOf(stepExecuteRecordsFuture).thenApply( e -> {
                HashMap<Long, List<StepExecuteRecord>> stepExecuteRecords = stepExecuteRecordsFuture.join();
                // 组装VO
                toSceneDetailInfo(recordIds, sceneRecordListVo, sceneExecuteRecordMap, stepExecuteRecords, stepOrders);
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
    public Long saveRecord(Long sceneId, List<Long> orderList, Integer status, Integer type) {
        try {
            Scene scene = sceneDetailRepository.querySceneById(sceneId);
            Long currentTime = System.currentTimeMillis();
            SceneExecuteRecord sceneExecuteRecord = SceneExecuteRecord.build(scene);
            sceneExecuteRecord.setExecuteTime(currentTime);
            sceneExecuteRecord.setStepOrderList(orderList);
            sceneExecuteRecord.setStatus(status);
            sceneExecuteRecord.setType(type);
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

    @Deprecated
    private HashMap<Long, SceneStepOrder> batchGetRunStepOrder(List<Long> recordIds) {
        HashMap<Long, SceneStepOrder> sceneStepOrderMap = stepOrderRepository.bacthQueryStepExeOrderByRecordIds(recordIds);
        sceneStepOrderMap.forEach((recordId, sceneStepOrder) -> {
            List<Long> orderList = sceneStepOrder.getOrderList();
            sceneStepOrder.setOrderList(orderList);
        });
        log.info("[SceneRecordServiceImpl:batchGetStepOrder] get step order, datas = {}", JSON.toJSONString(sceneStepOrderMap));
        return sceneStepOrderMap;
    }

    private void toSceneDetailInfo(List<Long> recordIds, SceneRecordListVo sceneRecordListVo,
                                   HashMap<Long, SceneExecuteRecord> sceneExecuteRecordMap,
                                   HashMap<Long, List<StepExecuteRecord>> stepExecuteRecordMap,
                                   HashMap<Long, List<Long>> stepOrders) {
        stepExecuteRecordMap.forEach((recordId, stepExecuteRecords) -> {
            List<Long> stepOrderList = stepOrders.get(recordId); // 场景执行时的顺序
            // 使用匿名比较器排序
            Collections.sort(stepExecuteRecords, new Comparator<StepExecuteRecord>() {
                @Override
                public int compare(StepExecuteRecord record1, StepExecuteRecord record2) {
                    return stepOrderList.indexOf(record1.getStepId()) - stepOrderList.indexOf(record2.getStepId());
                }
            });
        });
        List<SceneExeRecordDto> sceneExeRecordDtos = new ArrayList<>(sceneExecuteRecordMap.size());
        recordIds.forEach((recordId) -> {
            List<StepExecuteRecord> stepExecuteRecords = stepExecuteRecordMap.get(recordId);
            SceneExeRecordDto sceneExeRecordDto = new SceneExeRecordDto();
            Integer stepNum = stepExecuteRecords.size();
            sceneExeRecordDto.setStepNum(stepNum);

            // 判读是否存在子场景执行记录
            List<StepExecuteRecord> sceneStepExecuteRecord = stepExecuteRecords.stream().filter(stepExecuteRecord ->
                    stepExecuteRecord.getSceneRecordId() != 0 || stepExecuteRecord.getSceneRecordId() != null)
                    .collect(Collectors.toList()); // 所有子场景执行步骤
            if (sceneStepExecuteRecord.size() > 0) {
                log.info("[SceneRecordServiceImpl:toSceneDetailInfo] scene {} has son scene",
                        sceneExecuteRecordMap.get(recordId).getSceneId());
                // 由于当前执行步骤中，存在子场景，所以需要对子场景的执行步骤情况进行查询编排
                buildSceneWithSceneExeRecordDto(sceneExeRecordDto, sceneExecuteRecordMap.get(recordId), stepExecuteRecords);
            } else {
                log.info("[SceneRecordServiceImpl:toSceneDetailInfo] scene {} don't have son scene",
                        sceneExecuteRecordMap.get(recordId).getSceneId());
                buildSceneExeRecordDto(sceneExeRecordDto, sceneExecuteRecordMap.get(recordId), stepExecuteRecords);
            }
            sceneExeRecordDtos.add(sceneExeRecordDto);
        });
        sceneRecordListVo.setSceneExeRecordDtos(sceneExeRecordDtos);
    }


    // 不具有子场景执行记录的构造
    private void buildSceneExeRecordDto(SceneExeRecordDto sceneExeRecordDto,
                                   SceneExecuteRecord sceneExecuteRecord,
                                   List<StepExecuteRecord> stepExecuteRecords) {
//        Integer stepNum = stepExecuteRecords.size();
//        sceneExeRecordDto.setStepNum(stepNum);
        // 根据步骤的执行状态来确定当前场景的执行结果
        sceneExeRecordDto.setStatus(StepRunResultEnum.stepStatusToSceneStatus(stepExecuteRecords));
        // 场景信息构造
        sceneExeRecordDto.setSceneExeInfoDto(new SceneExeInfoDto());
        SceneExeInfoDto.build(sceneExeRecordDto.getSceneExeInfoDto(), sceneExecuteRecord);
        // 步骤执行信息构造
        List<StepExeRecordInfo> stepExeInfos = buildStepExeInfos(stepExecuteRecords, StepTypeEnum.STEP.getType());
        sceneExeRecordDto.setStepExeInfos(stepExeInfos);
    }

    // 具有子场景执行记录的构造
    private void buildSceneWithSceneExeRecordDto(SceneExeRecordDto sceneExeRecordDto,
                                        SceneExecuteRecord sceneExecuteRecord,
                                        List<StepExecuteRecord> stepExecuteRecords) {
        Integer stepNum = 0;
        // 场景信息构造
        sceneExeRecordDto.setSceneExeInfoDto(new SceneExeInfoDto());
        SceneExeInfoDto.build(sceneExeRecordDto.getSceneExeInfoDto(), sceneExecuteRecord);
        // 步骤执行信息构造
        List<StepExeRecordInfo> stepExeInfos = new ArrayList<>();
        for (StepExecuteRecord stepExecuteRecord : stepExecuteRecords) {
            StepExeRecordInfo stepExeInfo = new StepExeRecordInfo();
            if (stepExecuteRecord.getSceneRecordId() > 0) {
                // 子场景执行记录处理
                Long sceneRunRecordId = stepExecuteRecord.getSceneRecordId();
                List<StepExecuteRecord> sceneStepExecuteRecords =
                        stepExecuteRecordRepository.queryStepExecuteRecordByRecordId(sceneRunRecordId); // 子场景的单步骤执行记录
//                stepNum += sceneStepExecuteRecords.size();
                // 编排顺序
                SceneExecuteRecord sonSceneExecuteRecord =
                        sceneExecuteRecordRepository.getSceneExeRecordById(sceneRunRecordId); // 子场景执行记录
                List<Long> stepOrderList = sonSceneExecuteRecord.getStepOrderList(); // 子场景步骤执行顺序
                Collections.sort(sceneStepExecuteRecords, new Comparator<StepExecuteRecord>() {
                    @Override
                    public int compare(StepExecuteRecord record1, StepExecuteRecord record2) {
                        return stepOrderList.indexOf(record1.getStepId()) - stepOrderList.indexOf(record2.getStepId());
                    }
                });
                List<StepExeRecordInfo> sceneStepExeInfos =
                        buildStepExeInfos(sceneStepExecuteRecords, StepTypeEnum.SCENE.getType());
                stepExeInfo = sceneStepExeInfos.get(0);
            } else {
                // 单步骤
//                stepNum += 1;
                List<StepExecuteRecord> stepExecuteRecordList = new ArrayList<>();
                stepExecuteRecordList.add(stepExecuteRecord);
                List<StepExeRecordInfo> stepExeInfoList =
                        buildStepExeInfos(stepExecuteRecordList, StepTypeEnum.STEP.getType());
                stepExeInfo = stepExeInfoList.get(0);
            }
            stepExeInfos.add(stepExeInfo);
        }
        sceneExeRecordDto.setStepExeInfos(stepExeInfos);
//        sceneExeRecordDto.setStepNum(stepNum);
        // 根据步骤的执行状态来确定当前场景的执行结果
        sceneExeRecordDto.setStatus(StepRunResultEnum.stepStatusToSceneStatus(stepExecuteRecords));
    }


    private List<StepExeRecordInfo> buildStepExeInfos(List<StepExecuteRecord> stepExecuteRecords, Integer type) {
        List<StepExeRecordInfo> stepExeInfos = new ArrayList<>();
        if (!stepExecuteRecords.isEmpty()) {
            if (type == StepTypeEnum.STEP.getType()) {
                // 单步骤执行
                for (StepExecuteRecord stepExecuteRecord : stepExecuteRecords) {
                    StepExeRecordInfo stepExeInfo = new StepExeRecordInfo();
                    stepExeInfo.setType(StepTypeEnum.STEP.getType());
                    stepExeInfo.setStatus(stepExecuteRecord.getStatus());
                    StepExeRecordDto stepExeRecordDto = StepExeRecordDto.build(stepExecuteRecord);
                    stepExeInfo.setStepExeRecordDto(stepExeRecordDto);
                    stepExeInfo.setSceneStepExeRecordDto(null);
                    stepExeInfos.add(stepExeInfo);
                }
            } else if (type == StepTypeEnum.SCENE.getType()) {
                // 子场景执行
                List<StepExeRecordDto> sceneStepExeRecordDtos = new ArrayList<>();
                for (StepExecuteRecord stepExecuteRecord : stepExecuteRecords) {
                    StepExeRecordDto stepExeRecordDto = StepExeRecordDto.build(stepExecuteRecord);
                    sceneStepExeRecordDtos.add(stepExeRecordDto);
                }
                SceneStepExeRecordDto sceneStepExeRecordDto = new SceneStepExeRecordDto();
                sceneStepExeRecordDto.setStatus(StepRunResultEnum.stepStatusToSceneStatus(stepExecuteRecords));
                sceneStepExeRecordDto.setSceneStepExeRecordDtos(sceneStepExeRecordDtos);

                StepExeRecordInfo stepExeInfo = new StepExeRecordInfo();
                stepExeInfo.setType(StepTypeEnum.SCENE.getType());
                stepExeInfo.setStepExeRecordDto(null);
                stepExeInfo.setStatus(SceneStatusEnum.sceneStatusToStepStatus(sceneStepExeRecordDto.getStatus()));
                stepExeInfo.setSceneStepExeRecordDto(sceneStepExeRecordDto);
                stepExeInfos.add(stepExeInfo);
            }
        }
        return stepExeInfos;
    }






}
