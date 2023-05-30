package com.testframe.autotest.core.repository.dao;

import com.testframe.autotest.core.meta.po.ExeSet;
import com.testframe.autotest.core.meta.po.SceneDetail;
import com.testframe.autotest.core.repository.mapper.ExeSetMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class ExeSetDao {

    @Autowired
    private ExeSetMapper exeSetMapper;

    public Long updateExeSet(ExeSet exeSet) {
        Long curr = System.currentTimeMillis();
        if (exeSet.getId() == null || exeSet.getId() <= 0) {
            // 新增
            exeSet.setCreateTime(curr);
            exeSet.setUpdateTime(curr);
            exeSet.setIsDelete(0);
            if (exeSetMapper.insertSelective(exeSet) > 0) {
                return exeSet.getId();
            }
        } else {
            // 更新
            exeSet.setUpdateTime(curr);
            return exeSetMapper.updateByPrimaryKeySelective(exeSet) > 0 ? exeSet.getId() : 0L;
        }
        return 0L;
    }

    public ExeSet queryExeSetById(Long id) {
        return exeSetMapper.selectByPrimaryKey(id);
    }

    public List<ExeSet> querySetByName(String name) {
        List<ExeSet> exeSets = exeSetMapper.selectBySetName(name);
        if (CollectionUtils.isEmpty(exeSets)) {
            return Collections.EMPTY_LIST;
        }
        return exeSets;
    }

}
