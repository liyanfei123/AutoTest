package com.testframe.autotest.service.impl;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneExecuteRecordRepository;
import com.testframe.autotest.core.repository.StepExecuteRecordRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.meta.bo.SceneExecuteRecord;
import com.testframe.autotest.meta.bo.SceneStepOrder;
import com.testframe.autotest.meta.bo.StepExecuteRecord;
import com.testframe.autotest.meta.vo.SceneRecordListVo;
import com.testframe.autotest.service.SceneRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    private StepOrderRepository stepOrderRepository;

    @Override
    public SceneRecordListVo records(Long sceneId) {
        try {
            log.info("[SceneDetailImpl:query] query execute records in sceneId {}", sceneId);
            // 查询当前场景下的执行记录
            HashMap<Long, SceneExecuteRecord> sceneExecuteRecordMap = sceneExecuteRecordRepository.querySceneExecuteRecordBySceneId(sceneId);
            Set<Long> recordIdset = sceneExecuteRecordMap.keySet();
            List<Long> recordIds = new ArrayList<>(recordIdset);
            // 批量获取场景执行下的多个步骤执行顺序
            CompletableFuture<HashMap<Long, List<StepExecuteRecord>>> stepExecuteRecordsFuture = CompletableFuture
                    .supplyAsync(() -> batchGetStepExeInfo(recordIds));
            // 批量获取场景执行下的各个步骤的执行信息
            CompletableFuture<HashMap<Long, SceneStepOrder>> stepOrderFuture = CompletableFuture
                    .supplyAsync(() -> batchGetStepOrder(recordIds));

            SceneRecordListVo sceneRecordListVo = new SceneRecordListVo();
            return CompletableFuture.allOf(stepExecuteRecordsFuture, stepOrderFuture).thenApply( e -> {
                HashMap<Long, List<StepExecuteRecord>> stepExecuteRecords = stepExecuteRecordsFuture.join();
                HashMap<Long, SceneStepOrder> stepOrders = stepOrderFuture.join();

                // 组装VO
                sceneRecordListVo.setSceneId(sceneId);
                return sceneRecordListVo;
            }).join();

        } catch (Exception e) {
            log.error("[SceneDetailImpl:query] query execute records {} error, reason: ", sceneId, e.getMessage());
            throw new AutoTestException("查询失败");
        }
    }

    private HashMap<Long, List<StepExecuteRecord>> batchGetStepExeInfo(List<Long> recordIds) {
        HashMap<Long, List<StepExecuteRecord>> stepExecuteRecordMap = stepExecuteRecordRepository.bacchQueryStepExeRecord(recordIds);
        return stepExecuteRecordMap;

    }

    private HashMap<Long, SceneStepOrder> batchGetStepOrder(List<Long> recordIds) {
        HashMap<Long, SceneStepOrder> sceneStepOrderMap = stepOrderRepository.bacthQueryStepExeOrderByRecordIds(recordIds);
        sceneStepOrderMap.forEach((recordId, sceneStepOrder) -> {
            String orderStr = sceneStepOrder.getOrderStr();
            List<Long> orderList = SceneStepOrder.orderToList(orderStr);
            sceneStepOrder.setOrderList(orderList);
        });
        return sceneStepOrderMap;
    }

    private void toSceneDetailInfo(SceneRecordListVo sceneRecordListVo,
                                   HashMap<Long, SceneExecuteRecord> sceneExecuteRecordMap,
                                   HashMap<Long, List<StepExecuteRecord>> stepExecuteRecords,
                                   HashMap<Long, SceneStepOrder> stepOrders) {
        // 对场景步骤顺序重排
        stepExecuteRecords.forEach((recordId, stepExecuteRecord) -> {
            List<Long> stepOrderList = stepOrders.get(recordId).getOrderList();
            // 使用匿名比较器排序
            Collections.sort(stepExecuteRecord, new Comparator<StepExecuteRecord>() {
                @Override
                public int compare(StepExecuteRecord record1, StepExecuteRecord record2) {
                    return stepOrderList.indexOf(record1.getStepId()) - stepOrderList.indexOf(record1.getStepId());
                }
            });
        });
    }
}
