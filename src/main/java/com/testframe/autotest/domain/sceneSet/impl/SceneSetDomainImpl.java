package com.testframe.autotest.domain.sceneSet.impl;

import com.testframe.autotest.cache.service.SceneCacheService;
import com.testframe.autotest.cache.service.StepCacheService;
import com.testframe.autotest.core.enums.ExeOrderEnum;
import com.testframe.autotest.core.enums.OpenStatusEnum;
import com.testframe.autotest.core.enums.SetMemTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.ExeSetDo;
import com.testframe.autotest.core.meta.Do.SceneSetRelDo;
import com.testframe.autotest.core.meta.convertor.ExeSetConverter;
import com.testframe.autotest.core.meta.convertor.SceneSetRelConvertor;
import com.testframe.autotest.core.meta.po.SceneSetRel;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.repository.ExeSetRepository;
import com.testframe.autotest.core.repository.SceneSetRelRepository;
import com.testframe.autotest.domain.sceneSet.SceneSetDomain;
import com.testframe.autotest.meta.bo.SceneSetBo;
import com.testframe.autotest.meta.bo.SceneSetRelSceneBo;
import com.testframe.autotest.meta.bo.SceneSetRelStepBo;
import com.testframe.autotest.meta.dto.record.SceneSimpleExecuteDto;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.sceneSet.ExeSetDto;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelSceneDto;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelStepDto;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import com.testframe.autotest.meta.query.SceneSetRelQry;
import com.testframe.autotest.meta.vo.SceneSimpleInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SceneSetDomainImpl implements SceneSetDomain {

    @Autowired
    private ExeSetRepository exeSetRepository;

    @Autowired
    private SceneSetRelRepository sceneSetRelRepository;

    @Autowired
    private SceneCacheService sceneCacheService;

    @Autowired
    private StepCacheService stepCacheService;

    @Autowired
    private SceneSetRelConvertor sceneSetRelConvertor;

    @Autowired
    private ExeSetConverter exeSetConverter;

    @Override
    public Long updateSceneSet(ExeSetDto exeSetDto) {
        ExeSetDo exeSetDo = new ExeSetDo();
        try {
            if (exeSetDto.getSetId() != null && exeSetDto.getSetId() > 0) {
                exeSetDo = exeSetRepository.queryExeSetById(exeSetDto.getSetId());
                if (exeSetDo == null) {
                    throw new AutoTestException("场景集合id错误");
                }
                Boolean changeFlag = Boolean.FALSE;
                if (exeSetDto.getSetName().trim().length() > 0 && !exeSetDo.equals(exeSetDto.getSetName())) {
                    exeSetDo.setSetName(exeSetDto.getSetName());
                    changeFlag = Boolean.TRUE;
                }
                if (exeSetDto.getStatus() != null && !exeSetDo.equals(exeSetDto.getStatus())) {
                    exeSetDo.setStatus(exeSetDo.getStatus());
                    changeFlag = Boolean.TRUE;
                }
                if (!changeFlag) {
                    return exeSetDto.getSetId();
                }
            } else {
                exeSetDo.setSetName(exeSetDto.getSetName());
                exeSetDo.setStatus(exeSetDto.getStatus());
            }
            return exeSetRepository.updateExeSet(exeSetDo);
        } catch (AutoTestException e) {
            log.error("[SceneSetDomainImpl:updateSceneSet] expected error, reason = {}", e.getMessage());
            throw new AutoTestException(e.getMessage());
        } catch (Exception e) {
            log.error("[SceneSetDomainImpl:updateSceneSet] unexpected error, reason = {}", e.getMessage());
            throw new AutoTestException(e.getMessage());
        }
    }

    @Override
    public Boolean deleteSceneSet(Long setId) {
        // 先判断当前执行集下是否有其他关联的场景或步骤，若有，则不给删除
        Integer stepCount = sceneSetRelRepository.countSetRelBySetIdWithType(setId, SetMemTypeEnum.STEP.getType());
        Integer sceneCount = sceneSetRelRepository.countSetRelBySetIdWithType(setId, SetMemTypeEnum.SCENE.getType());
        if (stepCount + stepCount > 0) {
            throw new AutoTestException("当前执行集下有有效步骤");
        }
        Boolean flag = exeSetRepository.deleteExeSet(setId);
        if (flag == null) {
            throw new AutoTestException("场景集合id错误");
        }
        return flag;
    }

    @Override
    public Boolean deleteSceneSetRel(Long setId, Long sceneId, Long stepId) {
        Boolean flag = sceneSetRelRepository.deleteSceneSetRel(setId, sceneId, stepId);
        if (flag == null) {
            throw new AutoTestException("请输入正确的参数");
        }
        return flag;
    }

    @Override
    public List<Long> updateSceneSetRel(List<SceneSetRelSceneDto> sceneSetRelSceneDtos,
                                        List<SceneSetRelStepDto> sceneSetRelStepDtos) {
        if (sceneSetRelSceneDtos.size() > 0 && sceneSetRelStepDtos.size() > 0) {
            throw new AutoTestException("禁止步骤和场景一起添加");
        }
        List<SceneSetRelDo> sceneSetRelDos = new ArrayList<>();
        List<Long> failIds = new ArrayList<>();
        if (sceneSetRelSceneDtos.size() > 0) {
            // 关联场景
            for (SceneSetRelSceneDto sceneSetRelSceneDto : sceneSetRelSceneDtos) {
                SceneSetRelDo sceneSetRelDo = sceneSetRelRepository.querySceneIdSetRel(
                        new SceneSetRelQry(sceneSetRelSceneDto.getSetId(), sceneSetRelSceneDto.getSceneId(), 0L, null));
                if (sceneSetRelDo == null) {
                    // 新增
                    sceneSetRelDo = sceneSetRelConvertor.DtoToDo(sceneSetRelSceneDto);
                    sceneSetRelDo.setType(SetMemTypeEnum.SCENE.getType());
                    sceneSetRelDo.setStatus(OpenStatusEnum.OPEN.getType());
                    sceneSetRelDo.setSort(sceneSetRelSceneDto.getSort()); // 默认排序为0
                } else {
                    // 更新的话，判断是否需要更新
                    if ((sceneSetRelSceneDto.getStatus() == null ||
                            sceneSetRelDo.getStatus() == sceneSetRelSceneDto.getStatus()) // 状态未变
                        &&(sceneSetRelSceneDto.getSort() == null ||
                            sceneSetRelDo.getSort() == sceneSetRelSceneDto.getSort()) ) { // 顺序未变
                        continue;
                    }
                    sceneSetRelDo.setType(SetMemTypeEnum.SCENE.getType());
                    sceneSetRelDo.setStatus(sceneSetRelSceneDto.getStatus());
                    sceneSetRelDo.setSort(sceneSetRelSceneDto.getSort());
                }
                sceneSetRelDos.add(sceneSetRelDo);
            }
            failIds = sceneSetRelRepository.updateSceneSetRelWithScenes(sceneSetRelDos);
        } else if (sceneSetRelStepDtos.size() > 0) {
            // 关联步骤
            for (SceneSetRelStepDto sceneSetRelStepDto : sceneSetRelStepDtos) {
                SceneSetRelDo sceneSetRelDo = sceneSetRelRepository.querySceneIdSetRel(
                        new SceneSetRelQry(sceneSetRelStepDto.getSetId(), 0L, sceneSetRelStepDto.getStepId(), null));
                if (sceneSetRelDo == null) {
                    // 新增
                    sceneSetRelDo = sceneSetRelConvertor.DtoToDo(sceneSetRelStepDto);
                    sceneSetRelDo.setType(SetMemTypeEnum.STEP.getType());
                    sceneSetRelDo.setStatus(OpenStatusEnum.OPEN.getType());
                    sceneSetRelDo.setSort(sceneSetRelStepDto.getSort()); // 默认排序为0
                } else {
                    // 更新的话，判断是否需要更新
                    if ((sceneSetRelStepDto.getStatus() == null ||
                            sceneSetRelDo.getStatus() == sceneSetRelStepDto.getStatus()) // 状态未变
                        && (sceneSetRelStepDto.getSort() == null ||
                            sceneSetRelDo.getSort() == sceneSetRelStepDto.getSort())) { // 顺序未变
                        continue;
                    }
                    sceneSetRelDo.setType(SetMemTypeEnum.STEP.getType());
                    sceneSetRelDo.setStatus(sceneSetRelStepDto.getStatus());
                    sceneSetRelDo.setSort(sceneSetRelStepDto.getSort());
                }
                sceneSetRelDos.add(sceneSetRelDo);
            }
            failIds = sceneSetRelRepository.updateSceneSetRelWithSteps(sceneSetRelDos);
        }
        return failIds;
    }

    /**
     * 查询当前执行集下的关联场景或步骤
     * @param setId
     * @param type 0：关联的场景，1：关联的步骤
     * @return
     */
    @Override
    public SceneSetBo querySetBySetId(Long setId, Integer type, Integer status, PageQry pageQry) {
        SceneSetBo sceneSetBo = new SceneSetBo();
        ExeSetDo exeSetDo = exeSetRepository.queryExeSetById(setId);
        if (exeSetDo == null) {
            return sceneSetBo;
        }
        ExeSetDto exeSetDto = exeSetConverter.DoToDto(exeSetDo);
        sceneSetBo.setSceneSetDto(exeSetDto);
        switch (type) {
            case 0: // 关联的场景
                return this.querySceneRel(sceneSetBo, setId, SetMemTypeEnum.SCENE.getType(), status, pageQry);
            case 1:
                return this.querySceneRel(sceneSetBo, setId, SetMemTypeEnum.STEP.getType(), status, pageQry);
            default:
                return sceneSetBo;
        }
    }


    /**
     * 查找所有关联的场景，然后再进行分页
     * todo 添加缓存机制
     * @param sceneSetBo
     * @param setId
     * @param pageQry
     */
    private SceneSetBo querySceneRel(SceneSetBo sceneSetBo, Long setId, Integer type, Integer status, PageQry pageQry) {
        HashMap<Integer, List<SceneSetRelDo>> diffSortSceneMap = new HashMap<>();
        if (type == SetMemTypeEnum.SCENE.getType()) {
            diffSortSceneMap = sceneSetRelRepository.querySetRelBySetIdWithTypeAndStatus
                    (setId, SetMemTypeEnum.SCENE.getType(), status);
        } else if (type == SetMemTypeEnum.STEP.getType()) {
            diffSortSceneMap = sceneSetRelRepository.querySetRelBySetIdWithTypeAndStatus
                    (setId, SetMemTypeEnum.STEP.getType(), status);
        } else {
            return sceneSetBo;
        }

        if (diffSortSceneMap.isEmpty()) {
            sceneSetBo.setSceneSetRelSceneBos(Collections.EMPTY_LIST);
            sceneSetBo.setSceneSetRelStepBos(Collections.EMPTY_LIST);
            return sceneSetBo;
        }

        List<SceneSetRelDo> headRels = diffSortSceneMap.get(ExeOrderEnum.HEAD.getType());
        List<SceneSetRelDo> normalRels = diffSortSceneMap.get(ExeOrderEnum.NORMAL.getType());
        List<SceneSetRelDo> lastRels = diffSortSceneMap.get(ExeOrderEnum.LAST.getType());
        headRels.stream().sorted(Comparator.comparing(SceneSetRelDo::getUpdateTime).reversed()); // 按UpdateTime降序排列
        normalRels.stream().sorted(Comparator.comparing(SceneSetRelDo::getUpdateTime)); // 按UpdateTime升序排列
        lastRels.stream().sorted(Comparator.comparing(SceneSetRelDo::getUpdateTime)); // 按UpdateTime升序排列
        List<SceneSetRelDo> allRels = new ArrayList<>();
        allRels.addAll(headRels);
        allRels.addAll(normalRels);
        allRels.addAll(lastRels);

        Integer needSize = pageQry.getSize();
        Long offset = pageQry.getOffset();
        List<SceneSetRelDo> resRels = allRels.subList(offset.intValue(), needSize);

        if (type == SetMemTypeEnum.SCENE.getType()) {
            sceneSetBo.setSceneSetRelStepBos(Collections.EMPTY_LIST);
            List<Long> sceneIds = resRels.stream().map(SceneSetRelDo::getSceneId).collect(Collectors.toList());
            CompletableFuture<HashMap<Long, SceneDetailDto>> sceneDetailFuture = CompletableFuture.supplyAsync(()
                    -> sceneCacheService.getSceneDetailsFromCache(sceneIds));
            return CompletableFuture.allOf(sceneDetailFuture).thenApply(e -> {
                HashMap<Long, SceneDetailDto> sceneDetailDtoMap = sceneDetailFuture.join();
                return assembleScene(sceneSetBo, resRels, sceneDetailDtoMap);
            }).join();
        } else if (type == SetMemTypeEnum.STEP.getType()) {
            sceneSetBo.setSceneSetRelSceneBos(Collections.EMPTY_LIST);
            List<Long> stepIds = resRels.stream().map(SceneSetRelDo::getStepId).collect(Collectors.toList());
            CompletableFuture<HashMap<Long, StepDetailDto>> stepDetailFuture = CompletableFuture.supplyAsync(()
                    -> stepCacheService.getStepInfosFromCache(stepIds));
            List<SceneSetRelStepDto> sceneSetRelStepDtos = resRels.stream().map(resRel -> sceneSetRelConvertor.DoToDto(resRel))
                    .collect(Collectors.toList());
            return CompletableFuture.allOf(stepDetailFuture).thenApply(e -> {
                HashMap<Long, StepDetailDto> stepDetailDtoMap = stepDetailFuture.join();
                return assembleStep(sceneSetBo, resRels, stepDetailDtoMap);
            }).join();
        }
        return sceneSetBo;
    }

    private SceneSetBo assembleScene(SceneSetBo sceneSetBo, List<SceneSetRelDo> resRels, HashMap<Long, SceneDetailDto> sceneDetailDtoMap) {
        List<SceneSetRelSceneBo> sceneSetRelSceneBos = new ArrayList<>();
        for (SceneSetRelDo sceneSetRelDo : resRels) {
            SceneSetRelSceneBo sceneSetRelSceneBo = new SceneSetRelSceneBo();
            sceneSetRelSceneBo.setRelId(sceneSetRelDo.getRelId());
            sceneSetRelSceneBo.setSceneId(sceneSetRelDo.getSceneId());
            sceneSetRelSceneBo.setSetId(sceneSetRelDo.getSetId());
            sceneSetRelSceneBo.setStatus(sceneSetRelDo.getStatus());
            sceneSetRelSceneBo.setSort(sceneSetRelDo.getSort());
            SceneDetailDto sceneDetailDto = sceneDetailDtoMap.get(sceneSetRelDo.getSceneId());
            sceneSetRelSceneBo.setSceneName(sceneDetailDto.getSceneName());
            sceneSetRelSceneBo.setStepNum(sceneDetailDto.getStepNum());
            sceneSetRelSceneBos.add(sceneSetRelSceneBo);
        }
        sceneSetBo.setSceneSetRelSceneBos(sceneSetRelSceneBos);
        return sceneSetBo;
    }

    private SceneSetBo assembleStep(SceneSetBo sceneSetBo, List<SceneSetRelDo> resRels, HashMap<Long, StepDetailDto> stepDetailDtoMap) {
        List<SceneSetRelStepBo> sceneSetRelStepBos = new ArrayList<>();
        for (SceneSetRelDo sceneSetRelDo : resRels) {
            SceneSetRelStepBo sceneSetRelStepBo = new SceneSetRelStepBo();
            sceneSetRelStepBo.setRelId(sceneSetRelDo.getRelId());
            sceneSetRelStepBo.setStepId(sceneSetRelDo.getStepId());
            sceneSetRelStepBo.setSetId(sceneSetRelDo.getSetId());
            sceneSetRelStepBo.setStatus(sceneSetRelDo.getStatus());
            sceneSetRelStepBo.setSort(sceneSetRelDo.getSort());
            sceneSetRelStepBo.setStepName(stepDetailDtoMap.get(sceneSetRelDo.getStepId()).getStepName());
            sceneSetRelStepBos.add(sceneSetRelStepBo);
        }
        sceneSetBo.setSceneSetRelStepBos(sceneSetRelStepBos);
        return sceneSetBo;
    }

    /**
     * 分页查找，不一次性查找所有的数据，未实现
     * @param sceneSetBo
     * @param setId
     * @param sort 代表从那边开寻找，-2表示是第一页
     * @param pageQry
     * @return
     */
    private List<SceneSetRelDo> querySceneRelV2(SceneSetBo sceneSetBo, Long setId, Integer sort, PageQry pageQry) {
        // offset需要不断的切换
        Integer needSize = pageQry.getSize();
        pageQry.setSize(pageQry.getSize()+1); // 多找一个
        Long offset = Long.valueOf((pageQry.getPage()-1)*pageQry.getSize());
        pageQry.setOffset(offset);

        List<SceneSetRelDo> sceneSetRelDos = new ArrayList<>(needSize);

        Integer headCount = sceneSetRelRepository.countSetRelBySetId(setId, ExeOrderEnum.HEAD.getType());
        Integer normalCount = sceneSetRelRepository.countSetRelBySetId(setId, ExeOrderEnum.NORMAL.getType());
        Integer lastCount = sceneSetRelRepository.countSetRelBySetId(setId, ExeOrderEnum.LAST.getType());
        // 直接计算是否有下一页
        Integer total = headCount + normalCount + lastCount;
        HashMap<Integer, PageQry> searchQryMap = new HashMap<>();
        // ----headCount|----normalCount|----lastCount
        //      [-----] |               |               条件1
        //           [--|--]            |               条件2
        //           [--|---------------|----]          条件3
        //              |  [----]       |               条件4
        //              |  [------------|-----]         条件5
        //              |               |     [----]    条件6
        if (headCount >= offset + needSize) {
            PageQry headPageQry = new PageQry(offset, pageQry.getSize() + 1);
            searchQryMap.put(ExeOrderEnum.HEAD.getType(), headPageQry);
        } else if (headCount > offset && headCount < offset + needSize && headCount + normalCount >= offset + needSize) {
            PageQry headPageQry = new PageQry(offset, headCount - offset.intValue());
            searchQryMap.put(ExeOrderEnum.HEAD.getType(), headPageQry);
            PageQry normalQry = new PageQry(0, needSize - (headCount - offset.intValue()));
            searchQryMap.put(ExeOrderEnum.NORMAL.getType(), normalQry);
        } else if (headCount > offset && headCount < offset + needSize && headCount + normalCount < offset + needSize) {
            PageQry headPageQry = new PageQry(offset, headCount - offset.intValue());
            searchQryMap.put(ExeOrderEnum.HEAD.getType(), headPageQry);
            PageQry normalQry = new PageQry(0, normalCount);
            searchQryMap.put(ExeOrderEnum.NORMAL.getType(), normalQry);
            PageQry lastQry = new PageQry(0, needSize - (headCount - offset.intValue()) - normalCount);
            searchQryMap.put(ExeOrderEnum.LAST.getType(), lastQry);
        } else if (true) {
            return null;
        }






        // 先查顶部

            PageQry headPageQry = new PageQry(pageQry.getPage(), offset, pageQry.getSize()+1);
            List<SceneSetRelDo> headSceneSetRelDos = sceneSetRelRepository.querySetRelBySetId(setId, ExeOrderEnum.HEAD.getType(), headPageQry);
            if (sceneSetRelDos.size() == pageQry.getSize()) {
                // 说明多找了一个
                return headSceneSetRelDos;
            } else if (sceneSetRelDos.size() == needSize) {
                // 正好找到了满足要求的个数
                return headSceneSetRelDos;
            }
            if (!headSceneSetRelDos.isEmpty()) {
                sceneSetRelDos.addAll(headSceneSetRelDos);
            }



        // 查找正常的
        List<SceneSetRelDo> normalSceneSetRelDos = sceneSetRelRepository.querySetRelBySetId(setId,
                ExeOrderEnum.NORMAL.getType(), pageQry);
        if (!headSceneSetRelDos.isEmpty()) {

        } else {

        }

        if (!normalSceneSetRelDos.isEmpty() && !headSceneSetRelDos.isEmpty() && // 查找到过顶部场景
                normalSceneSetRelDos.size() >= needSize - headSceneSetRelDos.size()){ // 足够添加到列表后面
            // 需要使用正常的来补充
            sceneSetRelDos.addAll(normalSceneSetRelDos.subList(0, needSize - headSceneSetRelDos.size()));

        } else if (!normalSceneSetRelDos.isEmpty() && headSceneSetRelDos.isEmpty()) {

        }
        return null;
    }



}
