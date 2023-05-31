package com.testframe.autotest.service.impl;

import com.testframe.autotest.core.config.AutoTestConfig;
import com.testframe.autotest.core.enums.SceneExecuteEnum;
import com.testframe.autotest.core.enums.StepTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.repository.SceneDetailRepository;
import com.testframe.autotest.core.repository.SceneExecuteRecordRepository;
import com.testframe.autotest.core.repository.StepExecuteRecordRepository;
import com.testframe.autotest.core.repository.StepOrderRepository;
import com.testframe.autotest.domain.record.RecordDomain;
import com.testframe.autotest.domain.record.SetRecordDomain;
import com.testframe.autotest.meta.bo.SceneRecordBo;
import com.testframe.autotest.meta.bo.StepRecordBo;
import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import com.testframe.autotest.meta.query.RecordQry;
import com.testframe.autotest.meta.validator.SceneValidator;
import com.testframe.autotest.meta.vo.SceneExeInfoVo;
import com.testframe.autotest.meta.vo.SceneExeRecordVo;
import com.testframe.autotest.meta.vo.SceneRecordListVo;
import com.testframe.autotest.meta.vo.StepExeRecordInfo;
import com.testframe.autotest.service.SceneRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    @Autowired
    private RecordDomain recordDomain;

    @Autowired
    private SetRecordDomain setRecordDomain;

    /**
     * 查询当前场景的执行记录
     * 默认为20条，可配置
     * 不分页
     * @param sceneId
     * @return
     */
    @Override
    public SceneRecordListVo records(Long sceneId) {
        try {
            log.info("[SceneDetailImpl:query] query execute records in sceneId {}", sceneId);
            sceneValidator.sceneIsExist(sceneId);

            SceneRecordListVo sceneRecordListVo = new SceneRecordListVo();
            sceneRecordListVo.setSceneId(sceneId);
            // 查询当前场景下的执行记录 作为子场景执行的记录不查询
            // todo 增加分页查询逻辑
            RecordQry recordQry = new RecordQry();
            recordQry.setOffset(0);
            recordQry.setSize(autoTestConfig.getRecordSize()); // 固定20条记录
            recordQry.setType(SceneExecuteEnum.SINGLE.getType());
            Boolean needSet = autoTestConfig.getSceneRecordWithSet();
            List<SceneRecordBo> sceneRecordBos = recordDomain.sceneExeRecord(sceneId, needSet, recordQry);

            if (sceneRecordBos.isEmpty()) {
                // 当前场景从未执行过
                log.info("[SceneDetailImpl:query] scene {} no execute records", sceneId);
                sceneRecordListVo.setSceneExeRecordVos(Collections.EMPTY_LIST);
                return sceneRecordListVo;
            }

            HashMap<Long, SceneRecordBo> sceneRecordMap = new HashMap<>();  // 主键为场景执行记录id的场景执行信息map
            sceneRecordBos.forEach(sceneRecordDto -> {
                sceneRecordMap.put(sceneRecordDto.getSceneExecuteRecordDto().getRecordId(), sceneRecordDto);
            });
            List<SceneExeRecordVo> sceneExeRecordVos = buildSceneExeRecordVos(sceneRecordBos);
            sceneRecordListVo.setSceneExeRecordVos(sceneExeRecordVos);
            return sceneRecordListVo;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[SceneDetailImpl:query] query execute records {} error, reason: ", sceneId, e);
            throw new AutoTestException("查询执行记录失败");
        }
    }

    @Override
    public SceneExeRecordVo recordDetail(Long sceneRecordId) {
        log.info("[SceneDetailImpl:recordDetail] query execute record by sceneRecordId {}", sceneRecordId);
        if (sceneRecordId == null || sceneRecordId <= 0) {
            throw new AutoTestException("请输入正确的执行记录id");
        }
        SceneRecordBo sceneRecordBo = recordDomain.sceneExeRecordDetail(sceneRecordId);
        if (sceneRecordBo == null) {
            throw new AutoTestException("执行记录id错误");
        }
        List<SceneExeRecordVo> sceneExeRecordVos = buildSceneExeRecordVos(Collections.singletonList(sceneRecordBo));
        return sceneExeRecordVos.get(0);
    }

    public List<SceneExeRecordVo> buildSceneExeRecordVos(List<SceneRecordBo> sceneRecordBos) {
        List<SceneExeRecordVo> sceneExeRecordVos = new ArrayList<>();
        for (SceneRecordBo sceneRecordBo : sceneRecordBos) {
            List<StepRecordBo> stepRecordBos = sceneRecordBo.getStepRecordBos();
            SceneExecuteRecordDto sceneExecuteRecordDto = sceneRecordBo.getSceneExecuteRecordDto();
            SceneExeRecordVo sceneExeRecordVo = new SceneExeRecordVo();
            sceneExeRecordVo.setSceneRecordId(sceneExecuteRecordDto.getRecordId());
            sceneExeRecordVo.setSceneId(sceneRecordBo.getSceneExecuteRecordDto().getSceneId());
            sceneExeRecordVo.setStepNum(stepRecordBos.size());
            SceneExeInfoVo sceneExeInfoVo = SceneExeInfoVo.build(sceneExecuteRecordDto);
            sceneExeRecordVo.setSceneExeInfo(sceneExeInfoVo);
            sceneExeRecordVo.setStatus(sceneExeInfoVo.getStatus());
            List<StepExeRecordInfo> stepExeInfos = this.getStepExeInfos(stepRecordBos);
            sceneExeRecordVo.setStepExeInfos(stepExeInfos);
            sceneExeRecordVos.add(sceneExeRecordVo);
        }
        return sceneExeRecordVos;
    }

    private List<StepExeRecordInfo> getStepExeInfos(List<StepRecordBo> stepRecordBos){
        List<StepExeRecordInfo> stepExeInfos = new ArrayList<>();
        for (StepRecordBo stepRecordBo : stepRecordBos) {
            StepExeRecordInfo stepExeRecordInfo = new StepExeRecordInfo();
            stepExeRecordInfo.setType(stepRecordBo.getType());
            if (stepRecordBo.getType() == StepTypeEnum.STEP.getType()) {
                stepExeRecordInfo.setStepExecuteRecordDto(stepRecordBo.getStepExecuteRecordDto());
                stepExeRecordInfo.setSonSceneExeRecordVo(null);
                stepExeRecordInfo.setStatus(stepRecordBo.getStepExecuteRecordDto().getStatus());
            } else if (stepRecordBo.getType() == StepTypeEnum.SCENE.getType()) {
                stepExeRecordInfo.setStepExecuteRecordDto(null);

                SceneExeRecordVo sonSceneExeRecordVo = new SceneExeRecordVo();
                SceneExeInfoVo sonSceneExeInfoVo = SceneExeInfoVo.build(stepRecordBo.getSceneRecordBo().getSceneExecuteRecordDto()); // 子场景执行信息
                sonSceneExeRecordVo.setSceneExeInfo(sonSceneExeInfoVo);
                List<StepExeRecordInfo> sonStepExes = this.getStepExeInfos(stepRecordBo.getSceneRecordBo().getStepRecordBos());
                sonSceneExeRecordVo.setStepExeInfos(sonStepExes);
                sonSceneExeRecordVo.setStepNum(sonStepExes.size());
                sonSceneExeRecordVo.setStatus(sonSceneExeInfoVo.getStatus());

                stepExeRecordInfo.setStatus(sonSceneExeInfoVo.getStatus());
                stepExeRecordInfo.setSonSceneExeRecordVo(sonSceneExeRecordVo);
            }
            stepExeInfos.add(stepExeRecordInfo);
        }
        return stepExeInfos;
    }
}
