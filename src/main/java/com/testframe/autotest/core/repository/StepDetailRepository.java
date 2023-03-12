package com.testframe.autotest.core.repository;

import com.testframe.autotest.cache.ao.SceneStepRelCache;
import com.testframe.autotest.cache.ao.StepDetailCache;
import com.testframe.autotest.cache.ao.StepOrderCache;
import com.testframe.autotest.core.enums.StepOrderEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.*;
import com.testframe.autotest.core.meta.convertor.SceneStepConverter;
import com.testframe.autotest.core.meta.convertor.StepDetailConvertor;
import com.testframe.autotest.core.meta.convertor.StepOrderConverter;
import com.testframe.autotest.core.meta.po.SceneDetail;
import com.testframe.autotest.core.meta.po.SceneStep;
import com.testframe.autotest.core.meta.po.StepDetail;
import com.testframe.autotest.core.meta.po.StepOrder;
import com.testframe.autotest.core.repository.dao.SceneStepDao;
import com.testframe.autotest.core.repository.dao.StepDetailDao;
import com.testframe.autotest.core.repository.dao.StepOrderDao;
import com.testframe.autotest.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// 将所有涉及db操作的都统一放到一起，这样可以异常统一回归
@Component
@Slf4j
public class StepDetailRepository {

    @Autowired
    private StepDetailDao stepDetailDao;

    @Autowired
    private SceneStepDao sceneStepDao;

    @Autowired
    private StepOrderDao stepOrderDao;

    @Autowired
    private StepDetailCache stepDetailCache;

    @Autowired
    private StepOrderCache stepOrderCache;

    @Autowired
    private SceneStepRelCache sceneStepRelCache;

    @Autowired
    private StepDetailConvertor stepDetailConvertor;

    @Autowired
    private SceneStepConverter sceneStepConverter;

    @Autowired
    private StepOrderConverter stepOrderConverter;

    // 单步骤添加
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public Long saveStep(StepDo stepDo, StepOrderDo stepOrderDo) {
        try {
            Long stepId = this.saveSingleStep(stepDo);
            if (stepOrderDo.getOrderList() == null) { // 场景添加的第一个步骤
                List<Long> stepOrderList = new ArrayList<Long>(){{add(stepId);}};
                stepOrderDo.setOrderList(stepOrderList);
            } else {
                stepOrderDo.getOrderList().add(stepId);
            }
            StepOrder stepOrder = stepOrderConverter.DoToPo(stepOrderDo);
            if (!stepOrderDao.updateStepOrder(stepOrder)) {
                throw new AutoTestException("步骤执行顺序更新失败");
            }
            return stepId;
        } catch (AutoTestException e) {
            throw new AutoTestException(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchSaveStep(List<StepDo> stepDos) {
        List<Long> stepOrderList = new ArrayList<>(); // 新保存的步骤
        for (StepDo stepDo: stepDos) {
            try {
                Long stepId = this.saveSingleStep(stepDo);
                stepOrderList.add(stepId);
            } catch (AutoTestException e) {
                throw new AutoTestException(e.getMessage());
            }
        }
        if (stepOrderList == null || stepOrderList.isEmpty()) {
            return null;
        }
        // 保存场景步骤执行顺序
        Long sceneId = stepDos.get(0).getSceneStepRelDo().getSceneId();
        List<StepOrder> stepOrders = stepOrderDao.getStepOrderBySceneId(sceneId);
        // 步骤默认添加再最后
        StepOrder originStepOrder = stepOrders.stream().filter(
                stepOrder -> stepOrder.getType() == StepOrderEnum.BEFORE.getType())
                .collect(Collectors.toList()).get(0);
        List<Long> originOrder = StringUtils.orderToList(originStepOrder.getOrderList());
        originOrder.addAll(stepOrderList);
        originStepOrder.setOrderList(originOrder.toString());
        if (!stepOrderDao.updateStepOrder(originStepOrder)) {
            throw new AutoTestException("步骤执行顺序添加失败");
        }

        sceneStepRelCache.clearSceneStepRels(sceneId);
        stepOrderCache.clearBeforeStepOrderCache(sceneId);
        return stepOrderList;
    }


    // 单步骤更新
//    @Deprecated
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean updateStep(StepDo stepDo, StepOrderDo stepOrderDo) {
//        return this.updateSingleStep(stepDo);
//    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean batchUpdateAndSave(Long sceneId, List<StepDo> saveStepDos, List<StepDo> updateStepDos, List<Long> nowStepOrder) {
        try {
            List<Long> newStepIds = new ArrayList<>();
            if (saveStepDos != null) {
                for (StepDo stepDo: saveStepDos) {
                    Long stepId = this.saveSingleStep(stepDo);
                    newStepIds.add(stepId);
                }
            }

            if (updateStepDos != null) {
                for (StepDo stepDo: updateStepDos) {
                    this.updateSingleStep(stepDo);
                }
            }

            // 更新执行顺序
            List<StepOrder> stepOrders = stepOrderDao.getStepOrderBySceneIdAndType(sceneId, StepOrderEnum.BEFORE.getType());
            StepOrder stepOrder = stepOrders.get(0);
            List<Long> orderList = StringUtils.orderToList(stepOrder.getOrderList());
            if (orderList.isEmpty()) {
                orderList = newStepIds;
            } else {
                int j = 0;
                for (int i = 0; i < nowStepOrder.size(); i++) {
                    if (nowStepOrder.get(i) == -1L || nowStepOrder.get(i) == 0 || nowStepOrder.get(i) == null) {
                        nowStepOrder.set(i, newStepIds.get(j++));
                    }
                }
            }
            stepOrder.setOrderList(orderList.toString());
            if (!stepOrderDao.updateStepOrder(stepOrder)) {
                throw new AutoTestException("步骤执行顺序更新失败");
            }
            sceneStepRelCache.clearSceneStepRels(sceneId);
            stepOrderCache.clearBeforeStepOrderCache(sceneId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[SceneStepRepository:batchUpdateStep] update step error, reason ={}", e.getMessage());
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean batchUpdateStep(Long sceneId, List<StepDo> stepDos) {
        try {
            for (StepDo stepDo: stepDos) {
                this.updateSingleStep(stepDo);
            }
            sceneStepRelCache.clearSceneStepRels(sceneId);
            stepOrderCache.clearBeforeStepOrderCache(sceneId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[SceneStepRepository:batchUpdateStep] update step error, reason ={}", e.getMessage());
            return false;
        }
    }

    public StepDetailDo queryStepById(Long stepId) {
        StepDetail stepDetail = stepDetailDao.queryByStepId(stepId);
        if (stepDetail == null) {
            return null;
        } else {
            return stepDetailConvertor.PoToDo(stepDetail);
        }
    }

    public List<StepDetailDo> queryStepBySceneIds(List<Long> sceneIds) {
        List<StepDetail> stepDetails = stepDetailDao.queryStepBySceneId(sceneIds);
        if (stepDetails.isEmpty()) {
            return Collections.EMPTY_LIST;
        } else {
            List<StepDetailDo> stepDetailDos = stepDetails.stream().map(stepDetailConvertor::PoToDo)
                    .collect(Collectors.toList());
            return stepDetailDos;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteStep(List<SceneStepRelDo> sceneStepRelDos) {
        List<Long> removeStepIds = new ArrayList<>();
        for (SceneStepRelDo sceneStepRelDo : sceneStepRelDos) {
            SceneStep sceneStep = sceneStepConverter.DoToPO(sceneStepRelDo);
            sceneStep.setIsDelete(1);
            if (!sceneStepDao.updateSceneStep(sceneStep)) {
                throw new AutoTestException("删除步骤失败");
            }
            removeStepIds.add(sceneStepRelDo.getStepId());
        }

        // 更新步骤执行顺序
        Long sceneId = sceneStepRelDos.get(0).getSceneId();
        List<StepOrder> stepOrders = stepOrderDao.getStepOrderBySceneIdAndType(sceneId, StepOrderEnum.BEFORE.getType());
        StepOrder stepOrder = stepOrders.get(0);
        List<Long> orderList = StringUtils.orderToList(stepOrder.getOrderList());
        orderList.removeAll(removeStepIds);
        stepOrder.setOrderList(orderList.toString());
        if (!stepOrderDao.updateStepOrder(stepOrder)) {
            throw new AutoTestException("删除步骤失败");
        }

        stepDetailCache.clearStepDetailCaches(removeStepIds);
        sceneStepRelCache.clearSceneStepRels(sceneId);
        stepOrderCache.clearBeforeStepOrderCache(sceneId);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long copyStep(StepDo stepDo, StepOrderDo stepOrderDo) {
        Long stepId = this.saveSingleStep(stepDo);
        Collections.replaceAll(stepOrderDo.getOrderList(), -1L, stepId);
        // 需要更新执行顺序
        StepOrder stepOrder = stepOrderConverter.DoToPo(stepOrderDo);
        if (!stepOrderDao.updateStepOrder(stepOrder)) {
            throw new AutoTestException("步骤执行顺序更新失败");
        }
        sceneStepRelCache.clearSceneStepRels(stepDo.getSceneStepRelDo().getSceneId());
        stepOrderCache.clearBeforeStepOrderCache(stepDo.getSceneStepRelDo().getSceneId());
        return stepId;
    }


    @Transactional(rollbackFor = Exception.class)
    private Long saveSingleStep(StepDo stepDo) {
        StepDetail stepDetail = stepDetailConvertor.DoToPo(stepDo.getStepDetailDo());
        Long stepId = stepDetailDao.saveStepDetail(stepDetail);
        if (stepId == null) {
            throw new AutoTestException("步骤保存失败");
        }
        // 保存步骤-场景关联关系
        SceneStepRelDo sceneStepRelDo = stepDo.getSceneStepRelDo();
        sceneStepRelDo.setStepId(stepId);
        SceneStep sceneStep = sceneStepConverter.DoToPO(sceneStepRelDo);
        if (!sceneStepDao.saveSceneStep(sceneStep)) {
            throw new AutoTestException("步骤-场景关联关系保存失败");
        };
        stepDetailCache.clearStepDetailCache(stepDo.getStepDetailDo().getStepId());
        return stepId;
    }

    @Transactional(rollbackFor = Exception.class)
    private Boolean updateSingleStep(StepDo stepDo) {
        // 更新步骤
        if (stepDo.getStepDetailDo() != null) {
            StepDetail stepDetail = stepDetailConvertor.DoToPo(stepDo.getStepDetailDo());
            if (!stepDetailDao.updateStepDetail(stepDetail)) {
                throw new AutoTestException("步骤更新失败");
            }
        }

        // 更新步骤-场景关联关系
        if (stepDo.getSceneStepRelDo() != null) { // 存在不更新状态的情况
            SceneStep sceneStep = sceneStepConverter.DoToPO(stepDo.getSceneStepRelDo());
            if (!sceneStepDao.updateSceneStep(sceneStep)) {
                throw new AutoTestException("步骤-场景关联关系更新失败");
            };
        }
        stepDetailCache.clearStepDetailCache(stepDo.getStepDetailDo().getStepId());
        return true;
    }

}
