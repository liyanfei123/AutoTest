package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.enums.ExeOrderEnum;
import com.testframe.autotest.core.enums.OpenStatusEnum;
import com.testframe.autotest.core.enums.SetMemTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.meta.vo.common.PageVO;
import com.testframe.autotest.core.repository.SceneSetRelRepository;
import com.testframe.autotest.domain.sceneSet.SceneSetDomain;
import com.testframe.autotest.meta.bo.SceneSetBo;
import com.testframe.autotest.meta.bo.SceneSetRelSceneBo;
import com.testframe.autotest.meta.bo.SceneSetRelStepBo;
import com.testframe.autotest.meta.command.ExeSetUpdateCmd;
import com.testframe.autotest.meta.command.SceneSetRelCmd;
import com.testframe.autotest.meta.command.SceneSetRelDelCmd;
import com.testframe.autotest.meta.command.SceneSetRelTopCmd;
import com.testframe.autotest.meta.dto.sceneSet.ExeSetDto;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelSceneDto;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelStepDto;
import com.testframe.autotest.meta.validator.SceneSetValidator;
import com.testframe.autotest.meta.validator.SceneValidator;
import com.testframe.autotest.meta.validator.StepValidator;
import com.testframe.autotest.meta.vo.SetRelListVo;
import com.testframe.autotest.service.SceneSetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class SceneSetServiceImpl implements SceneSetService {

    @Autowired
    private SceneSetValidator sceneSetValidator;

    @Autowired
    private SceneValidator sceneValidator;

    @Autowired
    private StepValidator stepValidator;

    @Autowired
    private SceneSetDomain sceneSetDomain;

    @Autowired
    private SceneSetRelRepository sceneSetRelRepository;

    @Override
    public Long updateSceneSet(ExeSetUpdateCmd exeSetUpdateCmd) {
        log.info("[SceneSetServiceImpl:updateSceneSet] update scene set, exeSetUpdateCmd = {}",
                JSON.toJSONString(exeSetUpdateCmd));
        if (exeSetUpdateCmd.getStatus() == null) {
            exeSetUpdateCmd.setStatus(OpenStatusEnum.OPEN.getType()); // 默认开启
        }
        try {
            sceneSetValidator.checkSceneSetUpdate(exeSetUpdateCmd);
            ExeSetDto exeSetDto = new ExeSetDto();
            exeSetDto.setSetId(exeSetUpdateCmd.getSetId());
            exeSetDto.setSetName(exeSetDto.getSetName());
            exeSetDto.setStatus(exeSetUpdateCmd.getStatus());
            return sceneSetDomain.updateSceneSet(exeSetDto);
        } catch (Exception e) {
            log.error("[SceneSetServiceImpl:updateSceneSet] update scene set error, reason = {}", e.getStackTrace());
            throw new AutoTestException(e.getMessage());
        }
    }

    @Override
    public Boolean deleteSceneSet(Long setId) {
        log.info("[SceneSetServiceImpl:deleteSceneSet] delete scene set, setId = {}", setId);
        if (setId != null && setId <= 0L) {
            throw new AutoTestException("请输入正确的执行集合id");
        }
        try {
            sceneSetValidator.checkSceneSetValid(setId);
            sceneSetDomain.deleteSceneSet(setId);
            return true;
        } catch (AutoTestException e) {
            throw new AutoTestException(e.getMessage());
        } catch (Exception e) {
            log.error("[SceneSetServiceImpl:deleteSceneSet] delete scene set error, reason = {}", e.getStackTrace());
            return false;
        }
    }

    @Override
    public Boolean deleteSceneSetRel(SceneSetRelDelCmd sceneSetRelDelCmd) {
        log.info("[SceneSetServiceImpl:deleteSceneSetRel] delete scene set-rel, sceneSetRelDelCmd = {}",
                JSON.toJSONString(sceneSetRelDelCmd));
        if (sceneSetRelDelCmd.getSetId() != null && sceneSetRelDelCmd.getSetId() <= 0L) {
            throw new AutoTestException("请输入正确的执行集合id");
        }
        if (sceneSetRelDelCmd.getSetId() > 0 && sceneSetRelDelCmd.getSceneId() > 0) {
            throw new AutoTestException("不可同时删除场景和步骤关联");
        }
        if (sceneSetRelDelCmd.getSceneId() != null && sceneSetRelDelCmd.getSceneId() < 0) {
            throw new AutoTestException("请输入正确的关联场景id");
        }
        if (sceneSetRelDelCmd.getStepId() != null && sceneSetRelDelCmd.getStepId() < 0) {
            throw new AutoTestException("请输入正确的关联步骤id");
        }
        try {
            sceneSetValidator.checkSceneSetValid(sceneSetRelDelCmd.getSetId());
            return sceneSetDomain.deleteSceneSetRel(sceneSetRelDelCmd.getSetId(), sceneSetRelDelCmd.getSceneId(),
                    sceneSetRelDelCmd.getStepId());
        } catch (AutoTestException e) {
            throw new AutoTestException(e.getMessage());
        } catch (Exception e) {
            log.error("[SceneSetServiceImpl:deleteSceneSetRel] delete scene set-rel error, reason = {}", e.getStackTrace());
            return false;
        }
    }

    @Override
    public Boolean updateSceneSetRel(SceneSetRelCmd sceneSetRelCmd) {
        if (sceneSetRelCmd.getSetId() == null || sceneSetRelCmd.getSetId() < 0) {
            throw new AutoTestException("请输入正确的执行集id");
        }
        List<SceneSetRelSceneDto> sceneSetRelSceneDtos = sceneSetRelCmd.getSceneSetRelSceneDtos();
        List<SceneSetRelStepDto> sceneSetRelStepDtos = sceneSetRelCmd.getSceneSetRelStepDtos();
        log.info("[SceneSetServiceImpl:updateSceneSetRel] update scene set rel, sceneSetRelSceneDtos = {}, sceneSetRelStepDtos = {}",
                JSON.toJSONString(sceneSetRelSceneDtos), JSON.toJSONString(sceneSetRelStepDtos));
        if (sceneSetRelSceneDtos.size() > 0 && sceneSetRelSceneDtos.size() > 0) {
            throw new AutoTestException("场景和步骤不可同时添加");
        }
        Long setId = sceneSetRelCmd.getSetId();
        // 预处理
        sceneSetRelSceneDtos.forEach(sceneSetRelSceneDto -> {
            if (sceneSetRelSceneDto.getSceneId() == null || sceneSetRelSceneDto.getSceneId() < 0) {
                throw new AutoTestException("输入正确的场景id");
            }
            if (sceneSetRelSceneDto.getStatus() == null) {
                sceneSetRelSceneDto.setStatus(OpenStatusEnum.OPEN.getType());
            }
            if (sceneSetRelSceneDto.getSort() == null) {
                sceneSetRelSceneDto.setSort(ExeOrderEnum.NORMAL.getType());
            }
        });
        sceneSetRelStepDtos.forEach(sceneSetRelStepDto -> {
            if (sceneSetRelStepDto.getStepId() == null || sceneSetRelStepDto.getStepId() < 0) {
                throw new AutoTestException("输入正确的步骤id");
            }
            if (sceneSetRelStepDto.getStatus() == null) {
                sceneSetRelStepDto.setStatus(OpenStatusEnum.OPEN.getType());
            }
            if (sceneSetRelStepDto.getSort() == null) {
                sceneSetRelStepDto.setSort(ExeOrderEnum.NORMAL.getType());
            }
        });

        try {
            sceneSetValidator.checkSceneSetValid(setId);
            sceneSetRelSceneDtos.forEach(sceneSetRelSceneDto -> sceneSetRelSceneDto.setSetId(setId));
            sceneSetRelStepDtos.forEach(sceneSetRelStepDto -> sceneSetRelStepDto.setSetId(setId));
            sceneSetValidator.checkRelValid(sceneSetRelSceneDtos, sceneSetRelStepDtos);
            List<Long> failIds = sceneSetDomain.updateSceneSetRel(sceneSetRelSceneDtos, sceneSetRelStepDtos);
            return failIds.isEmpty() ? true : false;
        } catch (AutoTestException e) {
            throw new AutoTestException(e.getMessage());
        } catch (Exception e) {
            log.error("[SceneSetServiceImpl:updateSceneSetRel] update scene set rel error, reason = {}", e.getStackTrace());
            return false;
        }
    }

    @Override
    public Boolean topSetSceneOrStepRel(SceneSetRelTopCmd sceneSetRelTopCmd) {
        if (sceneSetRelTopCmd.getSetId() == null || sceneSetRelTopCmd.getSetId() < 0) {
            throw new AutoTestException("请输入正确的执行集id");
        }
        SceneSetRelSceneDto sceneSetRelSceneDto = sceneSetRelTopCmd.getSceneSetRelSceneDto();
        SceneSetRelStepDto sceneSetRelStepDto = sceneSetRelTopCmd.getSceneSetRelStepDto();
        log.info("[SceneSetServiceImpl:topSetSceneOrStepRel] update scene set rel, sceneSetRelSceneDto = {}, sceneSetRelStepDto = {}",
                JSON.toJSONString(sceneSetRelSceneDto), JSON.toJSONString(sceneSetRelStepDto));
        if (sceneSetRelSceneDto != null && sceneSetRelSceneDto != null) {
            throw new AutoTestException("场景和步骤不可同时修改置顶状态");
        }

        Long setId = sceneSetRelTopCmd.getSetId();
        // 预处理
        if (sceneSetRelSceneDto != null) {
            sceneSetRelSceneDto.setSetId(setId);
            if (sceneSetRelSceneDto.getSceneId() == null || sceneSetRelSceneDto.getSceneId() < 0) {
                throw new AutoTestException("输入正确的场景id");
            }
            if (sceneSetRelSceneDto.getSort() == null) {
                sceneSetRelSceneDto.setSort(ExeOrderEnum.NORMAL.getType());
            }
        }
        if (sceneSetRelStepDto != null) {
            sceneSetRelStepDto.setSetId(setId);
            if (sceneSetRelStepDto.getStepId() == null || sceneSetRelStepDto.getStepId() < 0) {
                throw new AutoTestException("输入正确的步骤id");
            }
            if (sceneSetRelStepDto.getSort() == null) {
                sceneSetRelSceneDto.setSort(ExeOrderEnum.NORMAL.getType());
            }
        }

        try {
            sceneSetValidator.checkSceneSetValid(setId);
            sceneSetValidator.checkRelValid(Collections.singletonList(sceneSetRelSceneDto),
                    Collections.singletonList(sceneSetRelStepDto));
            List<Long> failIds = sceneSetDomain.updateSceneSetRel(Collections.singletonList(sceneSetRelSceneDto),
                    Collections.singletonList(sceneSetRelStepDto));
            return failIds.isEmpty() ? true : false;
        } catch (AutoTestException e) {
            throw new AutoTestException(e.getMessage());
        } catch (Exception e) {
            log.error("[SceneSetServiceImpl:topSetSceneOrStepRel] update scene set rel error, reason = {}", e.getStackTrace());
            return false;
        }
    }

    @Override
    public SetRelListVo querySetRels(Long setId, Integer status, Integer type, Integer page, Integer pageSize) {
        PageQry pageQry = new PageQry();
        Long offset = Long.valueOf((page-1)*pageSize);
        pageQry.setOffset(offset);
        pageQry.setSize(pageSize+1); // 多找一个
        log.info("[SceneSetServiceImpl:querySetScenes] query scenes, setId = {}, status = {}, pageQry = {}",
                setId, status, JSON.toJSONString(pageQry));
        sceneSetValidator.checkSceneSetValid(setId);
        if (status != -1 && !OpenStatusEnum.getTypes().contains(status)) {
            throw new AutoTestException("请输入正确的状态");
        }
        SetRelListVo setRelListVo = new SetRelListVo();
        PageVO pageVO = new PageVO();
        SceneSetBo sceneSetBo;
        int totalRel;
        Long lastId;
        switch (SetMemTypeEnum.getByType(type)) {
            case SCENE:
                sceneSetBo = sceneSetDomain.querySetBySetId(setId, SetMemTypeEnum.SCENE.getType(), status, pageQry);
                totalRel = sceneSetRelRepository.countSetRelBySetIdWithType(setId, SetMemTypeEnum.SCENE.getType());
                List<SceneSetRelSceneBo> sceneSetRelSceneBos = sceneSetBo.getSceneSetRelSceneBos();
                if (totalRel == pageSize + 1) {
                    sceneSetRelSceneBos.remove(pageSize); // 移除掉多找到到的一个
                }
                lastId = sceneSetRelSceneBos.get(sceneSetRelSceneBos.size()-1).getRelId();
                setRelListVo.setScenes(sceneSetRelSceneBos);
            case STEP:
                sceneSetBo = sceneSetDomain.querySetBySetId(setId, SetMemTypeEnum.STEP.getType(), status, pageQry);
                totalRel = sceneSetRelRepository.countSetRelBySetIdWithType(setId, SetMemTypeEnum.STEP.getType());
                List<SceneSetRelStepBo> sceneSetRelStepBos = sceneSetBo.getSceneSetRelStepBos();
                if (totalRel == pageSize + 1) {
                    sceneSetRelStepBos.remove(pageSize); // 移除掉多找到到的一个
                }
                lastId = sceneSetRelStepBos.get(sceneSetRelStepBos.size()-1).getRelId();
                setRelListVo.setSteps(sceneSetRelStepBos);
            default:
                throw new AutoTestException(500, "类型错误");
        }
        pageVO.setPageNum(page);
        pageVO.setPageSize(pageSize);
        pageVO.setTotalCount(totalRel);
        pageVO.setTotalPage(totalRel / pageSize + (totalRel % pageSize == 0 ? 0 : 1));
        if (totalRel == pageSize + 1) {
            pageVO.setHasNext(true);
        } else {
            pageVO.setHasNext(false);
        }
        pageVO.setLastId(lastId);
        setRelListVo.setPageVO(pageVO);
        return setRelListVo;
    }


}
