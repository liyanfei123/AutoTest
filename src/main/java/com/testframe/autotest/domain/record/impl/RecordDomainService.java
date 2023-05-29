package com.testframe.autotest.domain.record.impl;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.cache.ao.SceneRecordCache;
import com.testframe.autotest.cache.service.RecordCacheService;
import com.testframe.autotest.core.enums.SceneExecuteEnum;
import com.testframe.autotest.core.enums.StepTypeEnum;
import com.testframe.autotest.core.meta.Do.SceneExecuteRecordDo;
import com.testframe.autotest.core.meta.Do.SetExecuteRecordDo;
import com.testframe.autotest.core.meta.Do.StepExecuteRecordDo;
import com.testframe.autotest.core.meta.convertor.SceneExecuteRecordConverter;
import com.testframe.autotest.core.meta.convertor.SetExecuteRecordConvertor;
import com.testframe.autotest.core.meta.convertor.StepExecuteRecordConverter;
import com.testframe.autotest.core.repository.SceneExecuteRecordRepository;
import com.testframe.autotest.core.repository.SetExecuteRecordRepository;
import com.testframe.autotest.core.repository.StepExecuteRecordRepository;
import com.testframe.autotest.domain.record.RecordDomain;
import com.testframe.autotest.domain.record.SetRecordDomain;
import com.testframe.autotest.meta.bo.StepRecordBo;
import com.testframe.autotest.meta.dto.record.SceneSimpleExecuteDto;
import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import com.testframe.autotest.meta.bo.SceneRecordBo;
import com.testframe.autotest.meta.dto.record.SetExecuteRecordDto;
import com.testframe.autotest.meta.dto.record.StepExecuteRecordDto;
import com.testframe.autotest.meta.query.RecordQry;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RecordDomainService implements RecordDomain, SetRecordDomain {

    @Autowired
    private SceneExecuteRecordRepository sceneExecuteRecordRepository;

    @Autowired
    private StepExecuteRecordRepository stepExecuteRecordRepository;

    @Autowired
    private SetExecuteRecordRepository setExecuteRecordRepository;

    @Autowired
    private SceneRecordCache sceneRecordCache;

    @Autowired
    private RecordCacheService recordCacheService;

    @Autowired
    private SceneExecuteRecordConverter sceneExecuteRecordConverter;

    @Autowired
    private StepExecuteRecordConverter stepExecuteRecordConverter;

    @Autowired
    private SetExecuteRecordConvertor setExecuteRecordConvertor;

    @Override
    public HashMap<Long, SceneSimpleExecuteDto> listRecSceneSimpleExeRecord(List<Long> sceneIds) {
        log.info("[SceneListInterImpl:listSceneSimpleExeRecord] sceneIds = {}", sceneIds);
        HashMap<Long, SceneSimpleExecuteDto> sceneExecuteDtoMap = new HashMap<>();
        if (sceneIds.isEmpty()) {
            return null;
        }
        sceneExecuteDtoMap = recordCacheService.RecSceneSimpleExeRecFromCache(sceneIds);
        log.info("[SceneListInterImpl:listSceneSimpleExeRecord] sceneIds = {}, result = {}",
                JSON.toJSONString(sceneIds), JSON.toJSONString(sceneExecuteDtoMap));
        return sceneExecuteDtoMap;
    }

    @Override
    public List<SceneRecordBo> sceneExeRecord(Long sceneId, Boolean needSet, RecordQry recordQry) {
        log.info("[SceneListInterImpl:getStepExeInRecord] sceneId = {}, needSet = {}, recordQry = {}",
                sceneId, needSet, JSON.toJSONString(recordQry));
        List<SceneRecordBo> sceneRecordBos = new ArrayList<>();
        List<SceneExecuteRecordDo> sceneExecuteRecordDos = sceneExecuteRecordRepository.
                querySceneExecuteRecordBySceneId(sceneId, needSet, recordQry);
        if (sceneExecuteRecordDos.isEmpty()) {
            return null;
        } else {
            // 获取每次执行时的步骤信息
            for (SceneExecuteRecordDo sceneExecuteRecordDo : sceneExecuteRecordDos) {
                SceneRecordBo sceneRecordBo = new SceneRecordBo();
                SceneExecuteRecordDto sceneExecuteRecordDto = sceneExecuteRecordConverter.DoToDto(sceneExecuteRecordDo);
                Long recordId = sceneExecuteRecordDto.getRecordId();
                List<Long> stepOrderList = sceneExecuteRecordDto.getStepOrderList();
                List<StepRecordBo> stepRecordBos = this.getStepExeInRecord(recordId,stepOrderList);
                sceneRecordBo.setSceneExecuteRecordDto(sceneExecuteRecordDto);
                sceneRecordBo.setStepRecordBos(stepRecordBos);
                sceneRecordBos.add(sceneRecordBo);
            }
        }
        // 按执行时间排序
        log.info("[SceneListInterImpl:getStepExeInRecord] scene exe reocrds = {}", JSON.toJSONString(sceneRecordBos));
        return sceneRecordBos;
    }


    /**
     * 获取当前执行记录下的所有步骤的执行信息
     * @param recordId 场景执行记录id
     * @param stepOrderList 执行时的顺序
     * @return
     */
    private List<StepRecordBo> getStepExeInRecord(Long recordId, List<Long> stepOrderList) {
        log.info("[SceneListInterImpl:getStepExeInRecord] recordId = {}, stepOrderList = {}", recordId, stepOrderList);
        List<StepExecuteRecordDo> stepExecuteRecordDos = stepExecuteRecordRepository.queryStepExecuteRecordByRecordId(recordId);
        if (stepExecuteRecordDos.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<StepExecuteRecordDto> stepExecuteRecordDtos = stepExecuteRecordDos.stream()
                .map(stepExecuteRecordConverter::DoToDto).collect(Collectors.toList());
        // 根据执行顺序编排
        Collections.sort(stepExecuteRecordDtos, new Comparator<StepExecuteRecordDto>() {
            @Override
            public int compare(StepExecuteRecordDto step1, StepExecuteRecordDto step2) {
                return stepOrderList.indexOf(step1.getStepId()) - stepOrderList.indexOf(step2.getStepId());
            }
        });
        List<StepRecordBo> stepRecordBos = new ArrayList<>();
        for (StepExecuteRecordDto stepExecuteRecordDto : stepExecuteRecordDtos) {
            log.info("[SceneListInterImpl:getStepExeInRecord] build step exe record, stepId = {}",
                    stepExecuteRecordDto.getStepId());
            StepRecordBo stepRecordBo = new StepRecordBo();
            if (stepExecuteRecordDto.getSceneRecordId() > 0) {
                // 当前步骤为子场景
                stepRecordBo.setType(StepTypeEnum.SCENE.getType());
                stepRecordBo.setStepExecuteRecordDto(stepExecuteRecordDto);
                Long sonRecordId = stepExecuteRecordDto.getSceneRecordId();
                SceneExecuteRecordDo sonSceneExeRecordDo = sceneExecuteRecordRepository.getSceneExeRecordById(sonRecordId);
                if (sonSceneExeRecordDo == null) {
                    stepRecordBo.setSceneRecordBo(null);
                }
                List<Long> order = sonSceneExeRecordDo.getStepOrderList();
                SceneExecuteRecordDto sonSceneExeDto = sceneExecuteRecordConverter.DoToDto(sonSceneExeRecordDo);
                stepRecordBo.setStatus(sonSceneExeDto.getStatus());
                // 子场景执行信息 & 执行步骤
                SceneRecordBo sceneRecordBo = new SceneRecordBo();
                sceneRecordBo.setSceneExecuteRecordDto(sonSceneExeDto);
                List<StepRecordBo> sonStepRecordBos = this.getStepExeInRecord(sonRecordId, order);
                sceneRecordBo.setStepRecordBos(sonStepRecordBos);
                stepRecordBo.setSceneRecordBo(sceneRecordBo);
            } else {
                // 作为单独步骤执行
                stepRecordBo.setType(StepTypeEnum.STEP.getType());
                stepRecordBo.setStatus(stepExecuteRecordDto.getStatus());
                stepRecordBo.setStepExecuteRecordDto(stepExecuteRecordDto);
                stepRecordBo.setSceneRecordBo(null);
            }
            stepRecordBos.add(stepRecordBo);
        }
        log.info("[SceneListInterImpl:getStepExeInRecord] stepRecordBos size = {}", stepRecordBos.size());
        return stepRecordBos;
    }


    @Override
    public HashMap<Long, List<SceneRecordBo>> listSceneExeRecord(List<Long> sceneIds, Boolean needSet, RecordQry recordQry) {
        HashMap<Long, List<SceneRecordBo>> sceneExecuteDtoMap = new HashMap<>();
        if (sceneIds.isEmpty()) {
            return null;
        }
        for (Long sceneId : sceneIds) {
            List<SceneExecuteRecordDo> sceneExecuteRecordDos = sceneExecuteRecordRepository.
                    querySceneExecuteRecordBySceneId(sceneId, needSet, recordQry);
            List<SceneRecordBo> sceneRecordBos = sceneExeRecord(sceneId, needSet, recordQry);
            sceneExecuteDtoMap.put(sceneId, sceneRecordBos);
        }
        log.info("[SceneListInterImpl:batchGetSceneExeRecord] sceneIds = {}, needSet = {}, result = {}",
                JSON.toJSONString(sceneIds), needSet, JSON.toJSONString(sceneExecuteDtoMap));
        return sceneExecuteDtoMap;
    }


    @Override
    public Long updateSceneExeRecord(SceneExecuteRecordDto sceneExecuteRecordDto, List<StepExecuteRecordDto> stepExecuteRecordDtos) {
        try {
            if (sceneExecuteRecordDto.getRecordId() == null || sceneExecuteRecordDto.getRecordId() == 0) {
                // 新增
                SceneExecuteRecordDo sceneExecuteRecordDo = sceneExecuteRecordConverter.DtoToDo(sceneExecuteRecordDto);
                sceneExecuteRecordDo.setRecordId(null);
                Long recordId = sceneExecuteRecordRepository.saveSceneExecuteRecord(sceneExecuteRecordDo);
                log.info("[RecordDomainService:updateSceneExeRecord] add scene exe record, record = {}, recordId = {}",
                        JSON.toJSONString(sceneExecuteRecordDo), recordId);
                return recordId;
           } else {
                // 更新
                SceneExecuteRecordDo sceneExecuteRecordDo = sceneExecuteRecordConverter.DtoToDo(sceneExecuteRecordDto);
                Long recordId = sceneExecuteRecordDo.getRecordId(); // 场景执行记录id
                List<StepExecuteRecordDo> stepExecuteRecordDos = null;
                if (stepExecuteRecordDtos != null) {
                    stepExecuteRecordDos = stepExecuteRecordDtos.stream()
                            .map(stepExecuteRecordConverter::DtoToDo).collect(Collectors.toList());
                    stepExecuteRecordDos.forEach(stepExecuteRecordDo -> stepExecuteRecordDo.setRecordId(recordId));
                }
                Boolean flag = sceneExecuteRecordRepository.updateSceneExecuteRecord(sceneExecuteRecordDo, stepExecuteRecordDos);
                log.info("[RecordDomainService:updateSceneExeRecord] update scene exe record, scene record = {}, step record = {}, res = {}",
                        JSON.toJSONString(sceneExecuteRecordDo), JSON.toJSONString(stepExecuteRecordDos), flag);
                if (!flag) {
                    return 0L;
                }
                return sceneExecuteRecordDo.getRecordId();
           }
        } catch (Exception e) {
            log.error("[RecordDomainService:updateSceneExeRecord] error, reason = {}", e);
            return 0L;
        }
    }

    @Override
    public Long updateSetExeRecord(SetExecuteRecordDto setExecuteRecordDto) {
        try {
            SetExecuteRecordDo setExecuteRecordDo = setExecuteRecordConvertor.DtoToDo(setExecuteRecordDto);
            if (setExecuteRecordDto.getSetRecordId() == null || setExecuteRecordDto.getSetRecordId() == 0) {
                // 新增
                setExecuteRecordDo.setSetRecordId(null);
                Long setRecordId = setExecuteRecordRepository.saveSetExecuteRecord(setExecuteRecordDo);
                log.info("[RecordDomainService:updateSetExeRecord] save set exe record, record = {}, recordId = {}",
                        JSON.toJSONString(setExecuteRecordDo), setRecordId);
                return setRecordId;
            } else {
                // 更新
                Boolean flag = setExecuteRecordRepository.updateSetExecuteRecord(setExecuteRecordDo);
                log.info("[RecordDomainService:updateSetExeRecord] update set exe record, set record = {}, res = {}",
                        JSON.toJSONString(setExecuteRecordDo), flag);
                if (!flag) {
                    return 0L;
                }
                return setExecuteRecordDto.getSetRecordId();
            }
        } catch (Exception e) {
            log.error("[RecordDomainService:updateSceneExeRecord] error, reason = {}", e);
            return 0L;
        }
    }

    /**
     * 根据执行集执行记录id来获取其下的场景执行裂变
     * @param setRecordId
     * @return
     */
    @Override
    public List<SceneRecordBo> sceneExeRecordWithSetRecord(Long setRecordId) {
        log.info("[SceneListInterImpl:sceneExeRecordWithSetRecord] setRecordId = {}", setRecordId);
        List<SceneRecordBo> sceneRecordBos = new ArrayList<>();
        List<SceneExecuteRecordDo> sceneExecuteRecordDos = sceneExecuteRecordRepository.querySceneExecuteRecordBySetRecordId(setRecordId);
        if (sceneExecuteRecordDos.isEmpty()) {
            return Collections.EMPTY_LIST;
        } else {
            // 获取每次执行时的步骤信息
            for (SceneExecuteRecordDo sceneExecuteRecordDo : sceneExecuteRecordDos) {
                SceneRecordBo sceneRecordBo = new SceneRecordBo();
                SceneExecuteRecordDto sceneExecuteRecordDto = sceneExecuteRecordConverter.DoToDto(sceneExecuteRecordDo);
                Long recordId = sceneExecuteRecordDto.getRecordId();
                List<Long> stepOrderList = sceneExecuteRecordDto.getStepOrderList();
                List<StepRecordBo> stepRecordBos = this.getStepExeInRecord(recordId,stepOrderList);
                sceneRecordBo.setSceneExecuteRecordDto(sceneExecuteRecordDto);
                sceneRecordBo.setStepRecordBos(stepRecordBos);
                sceneRecordBos.add(sceneRecordBo);
            }
        }
        log.info("[SceneListInterImpl:sceneExeRecordWithSetRecord] setRecordId = {}, scene exe reocrds = {}",
                setRecordId, JSON.toJSONString(sceneRecordBos));
        return sceneRecordBos;
    }

    @Override
    public List<SetExecuteRecordDto> setExeRecord(Long setId, RecordQry recordQry) {
        log.info("[SceneListInterImpl:setExeRecord] setId = {}, recordQry", setId, JSON.toJSONString(recordQry));
        List<SetExecuteRecordDo> setExecuteRecordDos = setExecuteRecordRepository.querySetExeRecordBySetId(setId, recordQry);
        if (setExecuteRecordDos.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<SetExecuteRecordDto> setExecuteRecordDtos = new ArrayList<>();
        setExecuteRecordDos.forEach(setExecuteRecordDo -> {
            SetExecuteRecordDto setExecuteRecordDto = setExecuteRecordConvertor.DoToDto(setExecuteRecordDo);
            setExecuteRecordDtos.add(setExecuteRecordDto);
        });
        return setExecuteRecordDtos;
    }
}
