package com.testframe.autotest.service.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.enums.CategoryRelEnum;
import com.testframe.autotest.core.enums.ExeOrderEnum;
import com.testframe.autotest.core.enums.OpenStatusEnum;
import com.testframe.autotest.core.enums.SetMemTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.core.meta.Do.ExeSetDo;
import com.testframe.autotest.core.meta.Do.SceneSetRelDo;
import com.testframe.autotest.core.meta.common.http.HttpStatus;
import com.testframe.autotest.core.meta.convertor.ExeSetConverter;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.meta.vo.common.PageVO;
import com.testframe.autotest.core.repository.CategorySceneRepository;
import com.testframe.autotest.core.repository.ExeSetRepository;
import com.testframe.autotest.core.repository.SceneSetRelRepository;
import com.testframe.autotest.domain.category.CategorySceneDomain;
import com.testframe.autotest.domain.sceneSet.SceneSetDomain;
import com.testframe.autotest.meta.bo.SceneSetBo;
import com.testframe.autotest.meta.bo.SceneSetRelSceneBo;
import com.testframe.autotest.meta.bo.SceneSetRelStepBo;
import com.testframe.autotest.meta.command.ExeSetUpdateCmd;
import com.testframe.autotest.meta.command.SceneSetRelCmd;
import com.testframe.autotest.meta.command.SceneSetRelDelCmd;
import com.testframe.autotest.meta.command.SceneSetRelTopCmd;
import com.testframe.autotest.meta.dto.category.CategorySceneDto;
import com.testframe.autotest.meta.dto.sceneSet.ExeSetDto;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelSceneDto;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelStepDto;
import com.testframe.autotest.meta.model.SceneSetConfigModel;
import com.testframe.autotest.meta.validation.scene.SceneValidators;
import com.testframe.autotest.meta.validator.CategoryValidator;
import com.testframe.autotest.meta.validator.SceneSetValidator;
import com.testframe.autotest.meta.validator.StepValidator;
import com.testframe.autotest.meta.vo.SetListVo;
import com.testframe.autotest.meta.vo.SetRelListVo;
import com.testframe.autotest.service.SceneSetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SceneSetServiceImpl implements SceneSetService {

    @Autowired
    private SceneSetValidator sceneSetValidator;

    @Autowired
    private SceneValidators sceneValidators;

    @Autowired
    private StepValidator stepValidator;

    @Autowired
    private CategoryValidator categoryValidator;

    @Autowired
    private SceneSetDomain sceneSetDomain;

    @Autowired
    private CategorySceneDomain categorySceneDomain;

    @Autowired
    private SceneSetRelRepository sceneSetRelRepository;

    @Autowired
    private ExeSetRepository exeSetRepository;

    @Autowired
    private CategorySceneRepository categorySceneRepository;

    @Autowired
    private ExeSetConverter exeSetConverter;

    @Override
    public Long updateSceneSet(ExeSetUpdateCmd exeSetUpdateCmd) {
        log.info("[SceneSetServiceImpl:updateSceneSet] update scene set, exeSetUpdateCmd = {}",
                JSON.toJSONString(exeSetUpdateCmd));
        if (exeSetUpdateCmd.getSetId() == null && exeSetUpdateCmd.getStatus() == null) {
            exeSetUpdateCmd.setStatus(OpenStatusEnum.OPEN.getType()); // 默认开启
        }
        if (exeSetUpdateCmd.getCategoryId() != null && exeSetUpdateCmd.getCategoryId() > 0) {
            categoryValidator.checkCategoryId(exeSetUpdateCmd.getCategoryId());
        } else if (exeSetUpdateCmd.getSetId() == null) {
            throw new AutoTestException("请输入类目id");
        }
        sceneSetValidator.checkSceneSetUpdate(exeSetUpdateCmd);
        // 检查当前类目下是否存在相同标题的场景
        sceneSetValidator.checkTitleInCategory(exeSetUpdateCmd.getSetName(), exeSetUpdateCmd.getCategoryId());
        try {
            ExeSetDto exeSetDto = new ExeSetDto();
            exeSetDto.setSetId(exeSetUpdateCmd.getSetId());
            exeSetDto.setSetName(exeSetUpdateCmd.getSetName());
            exeSetDto.setStatus(exeSetUpdateCmd.getStatus());
            exeSetDto.setCategoryId(exeSetUpdateCmd.getCategoryId());
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
        CategorySceneDo categorySceneDo = categorySceneRepository.queryBySetId(setId);
        if (categorySceneDo == null) {
            log.warn("[SceneSetDomainImpl:querySetBySetId] set doesn't rel category!!!");
        } else {
            exeSetDto.setCategoryId(categorySceneDo.getCategoryId());
        }
        return exeSetDto;
    }

    // 根据名称搜索
    @Override
    public SetListVo querySetByName(String setName, Integer categoryId, Integer page, Integer size) {
        SetListVo setListVo = new SetListVo();
        if (setName == null || setName.equals("")) {
            setListVo.setPageVO(null);
            setListVo.setSets(null);
            return setListVo;
        }
        List<ExeSetDo> exeSetDos = exeSetRepository.queryExeSetsByName(setName);
        List<ExeSetDto> exeSetDtos = exeSetDos.stream().map(exeSetDo -> exeSetConverter.DoToDto(exeSetDo))
                .collect(Collectors.toList());
        exeSetDtos.forEach(exeSetDto -> {
            CategorySceneDo categorySceneDo =  categorySceneRepository.queryBySetId(exeSetDto.getSetId());
            exeSetDto.setCategoryId(categorySceneDo.getCategoryId());
        });
        if (categoryId != null && categoryId > 0) {
            categoryValidator.checkCategoryId(categoryId);
            // 过滤执行集
            exeSetDtos = exeSetDtos.stream().filter(exeSetDto -> exeSetDto.getCategoryId() == categoryId)
                    .collect(Collectors.toList());
        }
        Integer total = exeSetDtos.size();
        // 计算页数 同时判断是否有下一页
        Integer totalPage = total / size + (total % size == 0 ? 1 : 0);
        Boolean hasNext = true;
        if (total <= size) {
            hasNext = false;
        }

        PageVO pageVO = new PageVO();
        pageVO.setPageSize(size);
        pageVO.setTotalCount(Long.valueOf(total));
        pageVO.setHasNext(hasNext);
        pageVO.setTotalPage(totalPage);
        pageVO.setPageNum(page);
        int startIndex = (page-1)*size;
        if (startIndex >= total) {
            exeSetDtos = Collections.EMPTY_LIST;
        } else {
            int sub = total - (page - 1) * size >= size ? size : total - (page - 1) * size;
            int endIndex = startIndex + sub;
            // 包装数据
            exeSetDtos = exeSetDtos.subList(startIndex, endIndex);
        }
        setListVo.setSets(exeSetDtos);
        return setListVo;
    }

    @Override
    public List<ExeSetDto> queryRelByStepIdOrSceneId(Long stepId, Long sceneId) {
        return sceneSetDomain.queryRelByStepIdOrSceneId(stepId, sceneId);
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
                // 场景关联时的额外配置
                SceneSetConfigModel sceneSetConfigModel = sceneSetRelSceneDto.getSceneSetConfigModel();
                if (sceneSetConfigModel != null) {
                    if (sceneSetConfigModel.getTimeOutTime() == null) {
                        sceneSetConfigModel.setTimeOutTime(0);
                    }
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
    public void updateSceneSetConfig(Long relId, SceneSetConfigModel sceneSetConfigModel) {
        sceneSetValidator.checkSceneSetConfig(sceneSetConfigModel);
        SceneSetRelDo sceneSetRelDo = sceneSetRelRepository.querySceneSetRelById(relId);
        if (sceneSetRelDo == null) {
            throw new AutoTestException("关联id错误");
        }
        SceneSetConfigModel oldSceneSetConfigModel = JSON.parseObject(sceneSetRelDo.getExtInfo(), SceneSetConfigModel.class);
        if (oldSceneSetConfigModel != null) {
            // 判断是否需要更新
            if (oldSceneSetConfigModel.getTimeOutTime() == sceneSetConfigModel.getTimeOutTime()) {
                return;
            }
        }
        sceneSetRelDo.setExtInfo(JSON.toJSONString(sceneSetConfigModel));
        sceneSetRelRepository.updateSceneSetRelWithScenes(Arrays.asList(sceneSetRelDo));
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
        pageVO.setTotalCount(Long.valueOf(totalRel));
        pageVO.setTotalPage(totalRel / pageSize + (totalRel % pageSize == 0 ? 0 : 1));
        pageVO.setHasNext(hasNext);
        pageVO.setLastId(lastId);
        setRelListVo.setPageVO(pageVO);
        return setRelListVo;
    }


    @Override
    public Boolean moveCategoryId(Long setId, Integer oldCategoryId, Integer newCategoryId) {
        log.info("[SceneSetServiceImpl:moveCategoryId] move setId {} from {} to categoryId {}", JSON.toJSONString(setId),
                oldCategoryId, newCategoryId);
        if (oldCategoryId == newCategoryId || setId == null || setId < 0) {
            return false;
        }
        sceneSetValidator.checkSceneSetValid(setId);
        categoryValidator.checkCategoryId(newCategoryId);
        categoryValidator.checkCategoryId(oldCategoryId);
        CategorySceneDo categorySceneDo = categorySceneRepository.queryBySetId(setId);
        if (categorySceneDo.getCategoryId() != oldCategoryId) {
            throw new AutoTestException("当前执行集不属于该类目");
        }
        // 判断新的类目下是否会存在同名类目
        sceneSetValidator.checkTitleInCategory(setId, newCategoryId);
        CategorySceneDto categorySceneDto = new CategorySceneDto();
        categorySceneDto.setCategoryId(newCategoryId);
        categorySceneDto.setSetId(setId);
        categorySceneDomain.batchUpdateCategoryScene(oldCategoryId,
                Collections.singletonList(categorySceneDto), CategoryRelEnum.SET.getType());
        return true;
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
