package com.testframe.autotest.domain.step.impl;


import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.*;
import com.testframe.autotest.core.meta.convertor.StepDetailConvertor;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneStepRepository;
import com.testframe.autotest.core.repository.StepDetailRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.domain.step.StepDomain;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import com.testframe.autotest.meta.dto.step.StepSaveAndUpdateDto;
import com.testframe.autotest.meta.dto.step.StepsDto;
import com.testframe.autotest.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StepDomainImpl implements StepDomain {

    @Autowired
    private StepDetailRepository stepDetailRepository;

    @Autowired
    private StepOrderRepository stepOrderRepository;

    @Autowired
    private SceneDetailRepository sceneDetailRepository;

    @Autowired
    private SceneStepRepository sceneStepRepository;

    @Autowired
    private StepDetailConvertor stepDetailConvertor;

    private StepDetailDto stepInfo(Long stepId) {
        StepDetailDto stepDetailDto = new StepDetailDto();
        StepDetailDo stepDetailDo = stepDetailRepository.queryStepById(stepId);
        if (stepDetailDo == null) {
            return null;
        }
        SceneStepRelDo sceneStepRelDo = sceneStepRepository.queryByStepId(stepId);
        stepDetailDto.setSceneId(sceneStepRelDo.getSceneId());
        stepDetailDto.setStepId(stepId);
        stepDetailDto.setSonSceneId(stepDetailDo.getSonSceneId());
        stepDetailDto.setStepName(stepDetailDo.getStepName());
        stepDetailDto.setStepStatus(sceneStepRelDo.getStatus());
        stepDetailDto.setType(sceneStepRelDo.getType());
        stepDetailDto.setStepUIInfo(stepDetailDo.getStepInfo());
        return stepDetailDto;
    }

    @Override
    public List<StepDetailDto> listStepInfo(Long sceneId) {
        try {
            List<SceneStepRelDo> sceneStepRelDos = sceneStepRepository.querySceneStepsBySceneId(sceneId);
            if (sceneStepRelDos.isEmpty()) { // 当前场景下无步骤
                return Collections.EMPTY_LIST;
            }
            List<Long> stepIds = sceneStepRelDos.stream().map(sceneStepRelDo -> sceneStepRelDo.getStepId())
                    .collect(Collectors.toList());
            List<StepDetailDto> stepDetailDtos = new ArrayList<>();
            for (Long stepId : stepIds) {
                StepDetailDto stepDetailDto = this.stepInfo(stepId);
                stepDetailDtos.add(stepDetailDto);
            }
            StepOrderDo stepOrderDo = stepOrderRepository.queryBeforeStepRunOrder(sceneId);
            if (stepOrderDo == null) {
                return Collections.EMPTY_LIST;
            }
            List<Long> orgStepOrder = stepOrderDo.getOrderList();
            // 不改变步骤编排顺序
            Collections.sort(stepDetailDtos, new Comparator<StepDetailDto>() {
                @Override
                public int compare(StepDetailDto step1, StepDetailDto step2) {
                    return orgStepOrder.indexOf(step1.getStepId()) - orgStepOrder.indexOf(step2.getStepId());
                }
            });
            return stepDetailDtos;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    // todo 对未传入的步骤需要进行删除的操作 放到业务层去处理
    // todo 执行顺序的处理放到业务层去调用，在业务层分为步骤保存和步骤执行顺序两个业务逻辑处理
    // 业务层判断当前步骤id是否属于该场景
    // 执行顺序 [1,-1,3,-1,2,-1,9,12] 将-1的位置轮流替换，即可得到正常的执行顺序
    @Override
    public Boolean updateSteps(StepsDto stepsDto) {
        if (stepsDto.getStepDetailDtos().isEmpty()) {
            return true;
        }
        try {
            List<StepDetailDto> stepDetailDtos = stepsDto.getStepDetailDtos();
            List<StepDo> stepDos = this.buildDetailUpdate(stepDetailDtos);
            if (!stepDetailRepository.batchUpdateStep(stepDos)) {
                throw new AutoTestException("步骤更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private StepDetailDo judgeStepChange(StepDetailDo stepDetailDo, StepDetailDto stepDetailDto) {
        if (stepDetailDto.getStepName() != stepDetailDo.getStepName()
                || stepDetailDto.getStepUIInfo() != stepDetailDo.getStepInfo()
                || stepDetailDto.getSonSceneId() != stepDetailDo.getSonSceneId()) {
            stepDetailDo.setStepName(stepDetailDto.getStepName());
            stepDetailDo.setStepInfo(stepDetailDto.getStepUIInfo());
            stepDetailDo.setSonSceneId(stepDetailDto.getSonSceneId());
            return stepDetailDo;
        } else {
            return null;
        }
    }

    private SceneStepRelDo judgeRelChange(SceneStepRelDo sceneStepRelDo, StepDetailDto stepDetailDto) {
        if (stepDetailDto.getType() != sceneStepRelDo.getType()
                || stepDetailDto.getStepStatus() != sceneStepRelDo.getStatus()) {
            sceneStepRelDo.setType(stepDetailDto.getType());
            sceneStepRelDo.setStatus(stepDetailDto.getStepStatus());
            return sceneStepRelDo;
        } else {
            return null;
        }
    }

    public List<Long> saveSteps(StepsDto stepsDto) {
        if (stepsDto.getStepDetailDtos().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<StepDetailDto> stepDetailDtos = stepsDto.getStepDetailDtos();
            List<StepDo> stepDos = this.buildDetailSave(stepDetailDtos);
            List<Long> stepIds = stepDetailRepository.batchSaveStep(stepDos);
            return stepIds;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AutoTestException(e.getMessage());
        }
    }

    // 需要删除的步骤
    @Override
    public Boolean deleteSteps(Long sceneId, List<Long> stepIds) {
        // 查询原来的步骤执行顺序
//        StepOrderDo stepOrderDo = stepOrderRepository.queryBeforeStepRunOrder(sceneId);
//        if (stepOrderDo == null) {
//            throw new AutoTestException("当前场景下无步骤可删除");
//        }
        List<SceneStepRelDo> sceneStepRelDos = new ArrayList<>();
        for (Long stepId : stepIds) {
            SceneStepRelDo sceneStepRelDo = sceneStepRepository.queryByStepId(stepId);
            if (sceneStepRelDo == null) {
                throw new AutoTestException("当前步骤id未与任何场景绑定, 不支持删除");
            }
            if (sceneStepRelDo.getSceneId() != sceneId) {
                throw new AutoTestException("当前步骤id未与当前场景绑定");
            }
            if (this.judgeStepIsScene(sceneStepRelDo.getSceneId())) {
                throw new AutoTestException("当前步骤所在场景被其他场景关联, 不支持删除");
            }
            sceneStepRelDos.add(sceneStepRelDo);
        }
        return stepDetailRepository.deleteStep(sceneStepRelDos);
    }

    @Override
    public Long copyStep(Long sceneId, Long stepId, List<Long> stepOrderList) {
        try {
            StepDetailDo stepDetailDo = stepDetailRepository.queryStepById(stepId);
            if (stepDetailDo == null) {
                throw new AutoTestException("被复制的步骤id不存在");
            }
            SceneStepRelDo sceneStepRelDo = sceneStepRepository.queryByStepIdAndSceneId(stepId, sceneId);
            if (sceneStepRelDo == null) {
                throw new AutoTestException("请复制正确的步骤");
            }

            StepDo stepDo = new StepDo();
            stepDetailDo.setStepId(null);
            stepDetailDo.setStepName(stepDetailDo.getStepName() + RandomUtil.randomCode(8));
            sceneStepRelDo.setId(null);
            sceneStepRelDo.setStepId(null);
            stepDo.setStepDetailDo(stepDetailDo);
            stepDo.setSceneStepRelDo(sceneStepRelDo);

            StepOrderDo stepOrderDo = stepOrderRepository.queryBeforeStepRunOrder(sceneId);
            stepOrderDo.setOrderList(stepOrderList);

            return stepDetailRepository.copyStep(stepDo, stepOrderDo);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AutoTestException("步骤复制失败");
        }

    }

    @Override
    public Boolean updateAndSaveSteps(StepSaveAndUpdateDto stepSaveAndUpdateDto) {
        List<StepDetailDto> saveStepDetailDtos = stepSaveAndUpdateDto.getSaveStepDetailDtos();
        List<StepDo> saveStepDos = this.buildDetailSave(saveStepDetailDtos);
        List<StepDetailDto> updateStepDetailDtos = stepSaveAndUpdateDto.getUpdateStepDetailDtos();
        List<StepDo> updateStepDos = this.buildDetailUpdate(updateStepDetailDtos);
        return stepDetailRepository.batchUpdateAndSave(stepSaveAndUpdateDto.getSceneId(), saveStepDos,
                updateStepDos, stepSaveAndUpdateDto.getNowStepOrder());
    }

    @Override
    public Boolean needUpdate(Long stepId, StepDetailDto stepDetailDto) {
        StepDetailDo stepDetailDo = stepDetailRepository.queryStepById(stepId);
        if (stepDetailDo == null) {
            throw new AutoTestException("当前步骤id错误");
        }
        stepDetailDo = this.judgeStepChange(stepDetailDo, stepDetailDto);
        SceneStepRelDo sceneStepRelDo = sceneStepRepository.queryByStepId(stepDetailDto.getStepId());
        sceneStepRelDo = this.judgeRelChange(sceneStepRelDo, stepDetailDto);
        if (stepDetailDo != null || sceneStepRelDo != null) {
            return true;
        }
        return false;
    }


    // 判断当前子场景关联的场景是否被其他场景关联
    private Boolean judgeStepIsScene(Long sceneId) {
        List<Long> sceneIds = new ArrayList<Long>(){{add(sceneId);}};
        List<StepDetailDo> stepDetailDos = stepDetailRepository.queryStepBySceneIds(sceneIds); // 当前场景所附属的 父场景的步骤
        if (stepDetailDos.isEmpty()) {
            return false;
        }
        for (StepDetailDo stepDetailDo : stepDetailDos) {
            Long stepId = stepDetailDo.getStepId();
            SceneStepRelDo sceneStepRelDo = sceneStepRepository.queryByStepId(stepId);
            if (sceneStepRelDo == null) {
                continue;
            }
            SceneDetailDo sceneDetailDo = sceneDetailRepository.querySceneById(sceneStepRelDo.getSceneId());
            if (stepDetailDo == null) {
                continue;
            } else {
                return true;
            }
        }
        return false;
    }

    private List<StepDo> buildDetailSave(List<StepDetailDto> stepDetailDtos) {
        List<StepDo> stepDos = new ArrayList<>();
        for (StepDetailDto stepDetailDto : stepDetailDtos) {
            StepDo stepDo = new StepDo();
            StepDetailDo stepDetailDo = new StepDetailDo(null, stepDetailDto.getStepName(),
                    stepDetailDto.getSonSceneId(), stepDetailDto.getStepUIInfo());
            stepDo.setStepDetailDo(stepDetailDo);
            SceneStepRelDo sceneStepRelDo = new SceneStepRelDo(null, null, stepDetailDto.getSceneId(),
                    stepDetailDto.getStepStatus(), stepDetailDto.getType());
            stepDo.setSceneStepRelDo(sceneStepRelDo);
            stepDos.add(stepDo);
        }
        return stepDos;
    }

    private List<StepDo> buildDetailUpdate(List<StepDetailDto> stepDetailDtos) {
        List<StepDo> stepDos = new ArrayList<>();
        for (StepDetailDto stepDetailDto : stepDetailDtos) {
            StepDo stepDo = new StepDo();
            // 判断步骤信息是否需要发生变化
            if (stepDetailDto.getStepName() == null && stepDetailDto.getSonSceneId() == null
                    && stepDetailDto.getStepUIInfo() == null) {
                stepDo.setStepDetailDo(null);
            } else {
                StepDetailDo stepDetailDo = stepDetailRepository.queryStepById(stepDetailDto.getStepId());
                if (stepDetailDo == null) {
                    throw new AutoTestException("当前步骤id错误");
                }
                stepDetailDo = this.judgeStepChange(stepDetailDo, stepDetailDto);
                stepDo.setStepDetailDo(stepDetailDo);
            }
            // 判断步骤状态是否需要变化
            if (stepDetailDto.getStepStatus() == null && stepDetailDto.getType() == null) {
                stepDo.setSceneStepRelDo(null);
            } else {
                SceneStepRelDo sceneStepRelDo = sceneStepRepository.queryByStepId(stepDetailDto.getStepId());
                sceneStepRelDo = this.judgeRelChange(sceneStepRelDo, stepDetailDto);
                stepDo.setSceneStepRelDo(sceneStepRelDo);
            }
            stepDos.add(stepDo);
        }
        return stepDos;
    }


}
