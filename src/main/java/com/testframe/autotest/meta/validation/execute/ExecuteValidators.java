package com.testframe.autotest.meta.validation.execute;

import com.testframe.autotest.cache.ao.SceneDetailCache;
import com.testframe.autotest.cache.service.SceneCacheService;
import com.testframe.autotest.core.enums.*;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.ExeSetDo;
import com.testframe.autotest.core.meta.Do.StepOrderDo;
import com.testframe.autotest.core.meta.channel.ExecuteChannel;
import com.testframe.autotest.core.meta.convertor.SceneExecuteRecordConverter;
import com.testframe.autotest.core.meta.request.PageQry;
import com.testframe.autotest.core.meta.vo.common.Response;
import com.testframe.autotest.core.repository.ExeSetRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.domain.sceneSet.SceneSetDomain;
import com.testframe.autotest.domain.step.StepDomain;
import com.testframe.autotest.meta.bo.SceneSetBo;
import com.testframe.autotest.meta.bo.SceneSetRelSceneBo;
import com.testframe.autotest.meta.command.ExecuteCmd;
import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import com.testframe.autotest.meta.dto.record.SetExecuteRecordDto;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import com.testframe.autotest.meta.model.SceneSetConfigModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExecuteValidators {

    @Autowired
    private ExeSetRepository exeSetRepository;

    @Autowired
    private StepOrderRepository stepOrderRepository;
    @Autowired
    private SceneSetDomain sceneSetDomain;

    @Autowired
    private StepDomain stepDomain;

    @Autowired
    private SceneCacheService sceneCacheService;

    @Autowired
    private SceneExecuteRecordConverter sceneExecuteRecordConverter;

    public Response<ExecuteChannel> validateSet(ExecuteChannel executeChannel) {
        ExecuteCmd executeCmd = executeChannel.getExecuteCmd();
        if (executeCmd.getSetId() == null || executeCmd.getSetId() == 0) {
            executeChannel.setSetExecuteRecordDto(null);
            executeChannel.setSceneSetConfigModelHashMap(new HashMap<>());
            return Response.success(executeChannel);
        }
        // 检验执行集是否存在
        Long setId = executeCmd.getSetId();
        ExeSetDo exeSetDo = exeSetRepository.queryExeSetById(setId);
        if (exeSetDo == null) {
            throw new AutoTestException("执行集id错误");
        }

        // 目前只支持场景执行
        PageQry pageQry = new PageQry();
        pageQry.setSize(-1); // 查找所有
        SceneSetBo sceneSetBo = sceneSetDomain.querySetBySetId(setId, SetMemTypeEnum.SCENE.getType(),
                OpenStatusEnum.OPEN.getType(), pageQry);
        List<SceneSetRelSceneBo> sceneSetRelSceneBos = sceneSetBo.getSceneSetRelSceneBos();
        if (sceneSetRelSceneBos.isEmpty()) {
            throw new AutoTestException("当前执行集下无可执行场景");
        }

        // 生成执行集执行记录id
        SetExecuteRecordDto setExecuteRecordDto = new SetExecuteRecordDto();
        setExecuteRecordDto.setSetId(setId);
        setExecuteRecordDto.setSetName(exeSetDo.getSetName());
        setExecuteRecordDto.setStatus(SetRunResultEnum.NORUN.getType());
        executeChannel.setSetExecuteRecordDto(setExecuteRecordDto);

        List<Long> sceneIds = new ArrayList<>();
        HashMap<Long, SceneSetConfigModel> sceneSetConfigModelHashMap = new HashMap<>();
        sceneSetRelSceneBos.forEach(sceneSetRelSceneBo -> {
            Long sceneId = sceneSetRelSceneBo.getSceneId();
            SceneSetConfigModel sceneSetConfigModel = sceneSetRelSceneBo.getSceneSetConfigModel();
            sceneIds.add(sceneId);
            sceneSetConfigModelHashMap.put(sceneId, sceneSetConfigModel);
        });
        HashMap<Long, SceneDetailDto> sceneDetailDtoMap = sceneCacheService.getSceneDetailsFromCache(sceneIds);
        List<SceneDetailDto> sceneDetailDtos = sceneDetailDtoMap.values().stream().filter(Objects::nonNull)
                .collect(Collectors.toList());
        executeChannel.setSceneSetConfigModelHashMap(sceneSetConfigModelHashMap);
        executeChannel.setSceneDetailDtos(sceneDetailDtos);

        return Response.success(executeChannel);
    }

    public Response<ExecuteChannel> validateScene(ExecuteChannel executeChannel) {
        // 如果是执行集合，直接跳过
        ExecuteCmd executeCmd = executeChannel.getExecuteCmd();
        if (executeChannel.getSceneDetailDtos() != null && !executeChannel.getSceneDetailDtos().isEmpty()) {
            // 对于执行集下的所有场景生成场景执行记录
            buildSceneExecuteRecordDtos(executeChannel);
            return Response.success(executeChannel);
        }
        SceneDetailDto sceneDetailDto = sceneCacheService.getSceneDetailFromCache(executeCmd.getSceneId());
        if (sceneDetailDto == null) {
            throw new AutoTestException("请输入正确的场景id");
        }
        List<SceneDetailDto> sceneDetailDtos = Arrays.asList(sceneDetailDto);
        List sceneIds = sceneDetailDtos.stream().map(e -> e.getSceneId())
                .collect(Collectors.toList());
        // 判断当前是否有正常进行中的场景
//            HashMap<Long, SceneSimpleExecuteDto> recordMap = recordDomain.listRecSceneSimpleExeRecord(sceneIds);
//            SceneSimpleExecuteDto sceneSimpleExecuteDto = recordMap.get(sceneId);
//            if (sceneSimpleExecuteDto != null && sceneSimpleExecuteDto.getStatus() == SceneStatusEnum.ING.getType()) {
        // 只对单独执行的判断
//                throw new AutoTestException("当前场景正在执行中，勿重复执行");
//            }

        executeChannel.setSceneDetailDtos(sceneDetailDtos);
        buildSceneExecuteRecordDtos(executeChannel);
        return Response.success(executeChannel);
    }

    private void buildSceneExecuteRecordDtos(ExecuteChannel executeChannel) {
        int type = executeChannel.getType();
        List<SceneDetailDto> sceneDetailDtos = executeChannel.getSceneDetailDtos();
        if (sceneDetailDtos.isEmpty()) {
            return;
        }
//        HashMap<Long, List<Long>> stepRunOrderMap = new HashMap<>();
        HashMap<Long, SceneExecuteRecordDto> sceneExecuteRecordDtoMap = new HashMap<>();
        HashMap<Long, SceneSetConfigModel> sceneSetConfigModelHashMap = executeChannel.getSceneSetConfigModelHashMap();
        HashMap<Long, List<StepDetailDto>> stepDetailDtoHashMap = new HashMap<>();
        for (SceneDetailDto sceneDetailDto : sceneDetailDtos) {
            // 获取场景下的所有步骤
            Long sceneId = sceneDetailDto.getSceneId();
            List<StepDetailDto> stepDetailDtos = stepDomain.listStepInfo(sceneId);
            // 过滤掉执行状态关闭的步骤
            List<StepDetailDto> openSteps = stepDetailDtos.stream().filter(stepDetailDto ->
                            stepDetailDto.getStepStatus() == OpenStatusEnum.OPEN.getType())
                    .collect(Collectors.toList());
            if (type == SceneExecuteEnum.SINGLE.getType() && openSteps.isEmpty()) {
                // 当只有场景单独执行时会抛出该异常
                throw new AutoTestException("当前场景无可执行的步骤");
            }
            stepDetailDtoHashMap.put(sceneId, stepDetailDtos);

            // 生成场景执行记录
            StepOrderDo stepOrderDo = stepOrderRepository.queryBeforeStepRunOrder(sceneId);
            List<Long> stepOrderList = stepOrderDo.getOrderList();
            SceneExecuteRecordDto sceneExecuteRecordDto = sceneExecuteRecordConverter.buildSceneExecuteRecord(sceneDetailDto);
            if (sceneSetConfigModelHashMap != null) {
                // 执行集关联的
                SceneSetConfigModel sceneSetConfigModel = sceneSetConfigModelHashMap.get(sceneId);
                if (sceneSetConfigModel != null) {
                    sceneExecuteRecordDto.setWaitTime(sceneSetConfigModel.getTimeOutTime());
                }
            }
            sceneExecuteRecordDto.setStatus(SceneStatusEnum.INT.getType());
            sceneExecuteRecordDto.setType(type);
            sceneExecuteRecordDto.setStepOrderList(stepOrderList);
            if (type == SceneExecuteEnum.BELOW.getType()) {
                // 子场景执行不需要读取这些配置
                sceneExecuteRecordDto.setUrl(null);
                sceneExecuteRecordDto.setWaitType(null);
                sceneExecuteRecordDto.setWaitTime(null);
            }
            sceneExecuteRecordDtoMap.put(sceneId, sceneExecuteRecordDto);
        }
        executeChannel.setStepDetailDtoHashMap(stepDetailDtoHashMap);
        executeChannel.setSceneExecuteRecordDtoHashMap(sceneExecuteRecordDtoMap);
    }


}
