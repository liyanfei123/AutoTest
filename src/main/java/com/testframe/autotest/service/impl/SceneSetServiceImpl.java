package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.enums.ExeOrderEnum;
import com.testframe.autotest.core.enums.OpenStatusEnum;
import com.testframe.autotest.core.enums.SetMemTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.ExeSetDo;
import com.testframe.autotest.core.meta.Do.SceneSetRelDo;
import com.testframe.autotest.core.meta.common.http.HttpStatus;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.meta.vo.common.PageVO;
import com.testframe.autotest.core.repository.ExeSetRepository;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
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

    @Autowired
    private ExeSetRepository exeSetRepository;

    @Override
    public Long updateSceneSet(ExeSetUpdateCmd exeSetUpdateCmd) {
        log.info("[SceneSetServiceImpl:updateSceneSet] update scene set, exeSetUpdateCmd = {}",
                JSON.toJSONString(exeSetUpdateCmd));
        if (exeSetUpdateCmd.getSetId() == null && exeSetUpdateCmd.getStatus() == null) {
            exeSetUpdateCmd.setStatus(OpenStatusEnum.OPEN.getType()); // 默认开启
        }
        sceneSetValidator.checkSceneSetUpdate(exeSetUpdateCmd);
        try {
            ExeSetDto exeSetDto = new ExeSetDto();
            exeSetDto.setSetId(exeSetUpdateCmd.getSetId());
            exeSetDto.setSetName(exeSetUpdateCmd.getSetName());
            exeSetDto.setStatus(exeSetUpdateCmd.getStatus());
            return sceneSetDomain.updateSceneSet(exeSetDto);
        } catch (Exception e) {
            log.error("[SceneSetServiceImpl:updateSceneSet] update scene set error, reason = {}", e.getStackTrace());
            throw new AutoTestException(e.getMessage());
        }
    }

    @Override
    public ExeSetDto querySet(Long setId) {
        ExeSetDo exeSetDo = exeSetRepository.queryExeSetById(setId);
        if (exeSetDo == null) {
            throw new AutoTestException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "执行集id错误");
        }
        ExeSetDto exeSetDto = new ExeSetDto();
        exeSetDto.setSetId(exeSetDo.getSetId());
        exeSetDto.setSetName(exeSetDo.getSetName());
        exeSetDto.setStatus(exeSetDo.getStatus());
        return exeSetDto;
    }

    @Override
    public List<ExeSetDto> queryRelByStepId(Long stepId, Long sceneId) {
        List<SceneSetRelDo> sceneSetRelDos = new ArrayList<>();
        List<ExeSetDto> exeSetDtos = new ArrayList<>();
        if (sceneId > 0) {
            sceneSetRelDos = sceneSetRelRepository.querySetRelByStepIdOrSceneId(0L, sceneId);
        } else if (stepId > 0) {
            sceneSetRelDos = sceneSetRelRepository.querySetRelByStepIdOrSceneId(stepId, 0L);
        }
        sceneSetRelDos.forEach(sceneSetRelDo -> {
            Long setId = sceneSetRelDo.getSetId();
            ExeSetDo exeSetDo = exeSetRepository.queryExeSetById(setId);
            if (exeSetDo != null) {
                ExeSetDto exeSetDto = new ExeSetDto();
                exeSetDto.setSetId(exeSetDo.getSetId());
                exeSetDto.setSetName(exeSetDo.getSetName());
                exeSetDto.setStatus(exeSetDo.getStatus());
                exeSetDtos.add(exeSetDto);
            }
        });
        return exeSetDtos;
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
            log.error("[SceneSetServiceImpl:deleteSceneSet] delete scene set error, reason = {}", e);
            return false;
        }
    }

    @Override
    public Boolean deleteSceneSetRel(SceneSetRelDelCmd sceneSetRelDelCmd) {
        log.info("[SceneSetServiceImpl:deleteSceneSetRel] delete scene set-rel, sceneSetRelDelCmd = {}",
                JSON.toJSONString(sceneSetRelDelCmd));
        dealNullParam(sceneSetRelDelCmd);
        if (sceneSetRelDelCmd.getSetId() <= 0L) {
            throw new AutoTestException("请输入正确的执行集合id");
        }
        if (sceneSetRelDelCmd.getSceneId() > 0 && sceneSetRelDelCmd.getStepId() > 0) {
            throw new AutoTestException("不可同时删除场景和步骤关联");
        }
        if (sceneSetRelDelCmd.getSceneId() <= 0 || sceneSetRelDelCmd.getStepId() <= 0) {
            throw new AutoTestException("请输入正确的关联场景/步骤id");
        }
        try {
            sceneSetValidator.checkSceneSetValid(sceneSetRelDelCmd.getSetId());
            return sceneSetRelRepository.deleteSceneSetRel(sceneSetRelDelCmd.getSetId(), sceneSetRelDelCmd.getSceneId(),
                    sceneSetRelDelCmd.getStepId());
        } catch (AutoTestException e) {
            e.printStackTrace();
            throw new AutoTestException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[SceneSetServiceImpl:deleteSceneSetRel] delete scene set-rel error, reason = {}", e);
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

        try {
            if (sceneSetRelSceneDtos.size() > 0 && sceneSetRelStepDtos.size() > 0) {
                throw new AutoTestException("场景和步骤不可同时添加");
            }
            Long setId = sceneSetRelCmd.getSetId();
            // 预处理
            sceneSetRelSceneDtos.forEach(sceneSetRelSceneDto -> {
                if (sceneSetRelSceneDto.getSceneId() == null || sceneSetRelSceneDto.getSceneId() <= 0) {
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
                if (sceneSetRelStepDto.getStepId() == null || sceneSetRelStepDto.getStepId() <= 0) {
                    throw new AutoTestException("输入正确的步骤id");
                }
                if (sceneSetRelStepDto.getStatus() == null) {
                    sceneSetRelStepDto.setStatus(OpenStatusEnum.OPEN.getType());
                }
                if (sceneSetRelStepDto.getSort() == null) {
                    sceneSetRelStepDto.setSort(ExeOrderEnum.NORMAL.getType());
                }
            });

            sceneSetValidator.checkSceneSetValid(setId);
            sceneSetRelSceneDtos.forEach(sceneSetRelSceneDto -> sceneSetRelSceneDto.setSetId(setId));
            sceneSetRelStepDtos.forEach(sceneSetRelStepDto -> sceneSetRelStepDto.setSetId(setId));
            sceneSetValidator.checkRelValid(sceneSetRelSceneDtos, sceneSetRelStepDtos);
            List<Long> failIds = sceneSetDomain.updateSceneSetRel(sceneSetRelSceneDtos, sceneSetRelStepDtos);
            return failIds.isEmpty() ? true : false;
        } catch (AutoTestException e) {
            throw new AutoTestException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[SceneSetServiceImpl:updateSceneSetRel] update scene set rel error, reason = {}", e);
            return false;
        }
    }

    @Override
    public Boolean topSetSceneOrStepRel(SceneSetRelTopCmd sceneSetRelTopCmd) {
        if (sceneSetRelTopCmd.getSetId() == null || sceneSetRelTopCmd.getSetId() < 0) {
            throw new AutoTestException("请输入正确的执行集id");
        }
        log.info("[SceneSetServiceImpl:topSetSceneOrStepRel] update scene set rel, SceneSetRelTopCmd = {}",
                JSON.toJSONString(sceneSetRelTopCmd));
        dealNullParam(sceneSetRelTopCmd);
        if (sceneSetRelTopCmd.getSceneId() > 0 && sceneSetRelTopCmd.getStepId() > 0) {
            throw new AutoTestException("场景和步骤不可同时修改置顶状态");
        }
        if (sceneSetRelTopCmd.getSceneId() <= 0) {
            throw new AutoTestException("输入正确的场景id");
        }
        if (sceneSetRelTopCmd.getStepId() <= 0) {
            throw new AutoTestException("输入正确的步骤id");
        }
        Long setId = sceneSetRelTopCmd.getSetId();
        sceneSetValidator.checkSceneSetValid(setId);
        if (sceneSetRelTopCmd.getSort() == null) {
            sceneSetRelTopCmd.setSort(ExeOrderEnum.NORMAL.getType());
        }
        try {
            List<Long> failIds = new ArrayList<>();
            if (sceneSetRelTopCmd.getSceneId() != null && sceneSetRelTopCmd.getSceneId() > 0) {
                SceneSetRelSceneDto sceneSetRelSceneDto = new SceneSetRelSceneDto();
                sceneSetRelSceneDto.setSetId(setId);
                sceneSetRelSceneDto.setSceneId(sceneSetRelTopCmd.getSceneId());
                sceneSetRelSceneDto.setSort(sceneSetRelTopCmd.getSort());
                log.info("[SceneSetServiceImpl:topSetSceneOrStepRel] change scene top, sceneSetRelSceneDto = {}",
                        JSON.toJSONString(sceneSetRelSceneDto));
                sceneSetValidator.checkRelSortValid(Collections.singletonList(sceneSetRelSceneDto), Collections.EMPTY_LIST);
                failIds = sceneSetDomain.updateSceneSetRel(Collections.singletonList(sceneSetRelSceneDto),
                        Collections.EMPTY_LIST);
            } else if (sceneSetRelTopCmd.getSetId() != null && sceneSetRelTopCmd.getStepId() > 0) {
                SceneSetRelStepDto sceneSetRelStepDto = new SceneSetRelStepBo();
                sceneSetRelStepDto.setSetId(setId);
                sceneSetRelStepDto.setStepId(sceneSetRelTopCmd.getStepId());
                sceneSetRelStepDto.setSort(sceneSetRelTopCmd.getSort());
                log.info("[SceneSetServiceImpl:topSetSceneOrStepRel] change step top, sceneSetRelSceneDto = {}",
                        JSON.toJSONString(sceneSetRelStepDto));
                sceneSetValidator.checkRelSortValid(Collections.EMPTY_LIST, Collections.singletonList(sceneSetRelStepDto));
                failIds = sceneSetDomain.updateSceneSetRel(Collections.EMPTY_LIST,
                        Collections.singletonList(sceneSetRelStepDto));
            }
            return failIds.isEmpty() ? true : false;
        } catch (AutoTestException e) {
            throw new AutoTestException(e.getMessage());
        } catch (Exception e) {
            log.error("[SceneSetServiceImpl:topSetSceneOrStepRel] update scene set rel error, reason = {}", e);
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
        SceneSetBo sceneSetBo;
        int totalRel = 0;
        Long lastId = 0L;
        Boolean hasNext = false;
        switch (SetMemTypeEnum.getByType(type)) {
            case SCENE:
                sceneSetBo = sceneSetDomain.querySetBySetId(setId, SetMemTypeEnum.SCENE.getType(), status, pageQry);
                totalRel = sceneSetRelRepository.countSetRelBySetIdWithType(setId, SetMemTypeEnum.SCENE.getType());
                List<SceneSetRelSceneBo> sceneSetRelSceneBos = sceneSetBo.getSceneSetRelSceneBos();
                if (sceneSetRelSceneBos.size() == pageSize + 1) {
                    hasNext = true;
                    sceneSetRelSceneBos.remove(pageSize); // 移除掉多找到到的一个
                }
                if (sceneSetRelSceneBos.size() > 0) {
                    lastId = sceneSetRelSceneBos.get(sceneSetRelSceneBos.size() - 1).getRelId();
                }
                setRelListVo.setScenes(sceneSetRelSceneBos);
                setRelListVo.setSteps(Collections.EMPTY_LIST);
                break;
            case STEP:
                sceneSetBo = sceneSetDomain.querySetBySetId(setId, SetMemTypeEnum.STEP.getType(), status, pageQry);
                totalRel = sceneSetRelRepository.countSetRelBySetIdWithType(setId, SetMemTypeEnum.STEP.getType());
                List<SceneSetRelStepBo> sceneSetRelStepBos = sceneSetBo.getSceneSetRelStepBos();
                if (sceneSetRelStepBos.size() == pageSize + 1) {
                    hasNext = true;
                    sceneSetRelStepBos.remove(pageSize); // 移除掉多找到到的一个
                }
                if (sceneSetRelStepBos.size() > 0) {
                    lastId = sceneSetRelStepBos.get(sceneSetRelStepBos.size()-1).getRelId();
                }
                setRelListVo.setSteps(sceneSetRelStepBos);
                setRelListVo.setScenes(Collections.EMPTY_LIST);
                break;
            default:
                throw new AutoTestException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "类型错误");
        }
        PageVO pageVO = new PageVO();
        pageVO.setPageNum(page);
        pageVO.setPageSize(pageSize);
        pageVO.setTotalCount(totalRel);
        pageVO.setTotalPage(totalRel / pageSize + (totalRel % pageSize == 0 ? 0 : 1));
        pageVO.setHasNext(hasNext);
        pageVO.setLastId(lastId);
        setRelListVo.setPageVO(pageVO);
        return setRelListVo;
    }


    private void dealNullParam(SceneSetRelTopCmd sceneSetRelTopCmd) {
        if (sceneSetRelTopCmd.getSceneId() == null) {
            sceneSetRelTopCmd.setSceneId(0L);
        }
        if (sceneSetRelTopCmd.getStepId() == null) {
            sceneSetRelTopCmd.setStepId(0L);
        }
    }

    private void dealNullParam(SceneSetRelDelCmd sceneSetRelDelCmd) {
        if (sceneSetRelDelCmd.getSceneId() == null) {
            sceneSetRelDelCmd.setSceneId(0L);
        }
        if (sceneSetRelDelCmd.getStepId() == null) {
            sceneSetRelDelCmd.setStepId(0L);
        }
    }
}