package com.testframe.autotest.core.meta.convertor;

import com.testframe.autotest.core.meta.Do.ExeSetDo;
import com.testframe.autotest.core.meta.po.ExeSet;
import com.testframe.autotest.meta.dto.sceneSet.ExeSetDto;
import org.springframework.stereotype.Component;

@Component
public class ExeSetConverter {

    public ExeSet DoToPo(ExeSetDo exeSetDo) {
        ExeSet exeSet = new ExeSet();
        exeSet.setId(exeSetDo.getSetId());
        exeSet.setSetName(exeSetDo.getSetName());
        exeSet.setStatus(exeSetDo.getStatus());
        return exeSet;
    }


    public ExeSetDo PoToDo(ExeSet exeSet) {
        ExeSetDo exeSetDo = new ExeSetDo();
        exeSetDo.setSetId(exeSet.getId());
        exeSetDo.setSetName(exeSet.getSetName());
        exeSetDo.setStatus(exeSet.getStatus());
        return exeSetDo;
    }

    public ExeSetDto DoToDto(ExeSetDo exeSetDo) {
        ExeSetDto exeSetDto = new ExeSetDto();
        exeSetDto.setSetId(exeSetDo.getSetId());
        exeSetDto.setSetName(exeSetDo.getSetName());
        exeSetDto.setStatus(exeSetDo.getStatus());
        return exeSetDto;
    }
}
