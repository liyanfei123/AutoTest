package com.testframe.autotest.core.repository;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.ExeSetDo;
import com.testframe.autotest.core.meta.convertor.ExeSetConverter;
import com.testframe.autotest.core.meta.po.ExeSet;
import com.testframe.autotest.core.repository.dao.ExeSetDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class ExeSetRepository {

    @Autowired
    private ExeSetDao exeSetDao;

    @Autowired
    private ExeSetConverter exeSetConverter;

    @Transactional(rollbackFor = Exception.class)
    public Long updateExeSet(ExeSetDo exeSetDo) {
        ExeSet exeSet = exeSetConverter.DoToPo(exeSetDo);
        Long setId = exeSetDao.updateExeSet(exeSet);
        if (setId == null || setId == 0L) {
            throw new AutoTestException("执行集合更新失败");
        }
        return setId;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteExeSet(Long setId) {
        ExeSet exeSet = exeSetDao.queryExeSetById(setId);
        if (exeSet == null) {
            return null;
        }
        exeSet.setIsDelete(1);
        return exeSetDao.updateExeSet(exeSet) > 0L ? true : false;
    }

    public ExeSetDo queryExeSetById(Long setId) {
        ExeSet exeSet = exeSetDao.queryExeSetById(setId);
        if (exeSet == null) {
            return null;
        }
        return exeSetConverter.PoToDo(exeSet);
    }
}
