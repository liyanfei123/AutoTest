package com.testframe.autotest.core.repository.dao;


import com.testframe.autotest.core.meta.po.CategoryDetail;
import com.testframe.autotest.core.repository.mapper.CategoryDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class CategoryDetailDao {

    @Autowired
    private CategoryDetailMapper categoryDetailMapper;

    public Integer saveCategory(CategoryDetail categoryDetail) {
        Long currentTime = System.currentTimeMillis();
        categoryDetail.setCreateTime(currentTime);
        categoryDetail.setUpdateTime(currentTime);
        if (categoryDetailMapper.insertSelective(categoryDetail) > 0 ) {
            return categoryDetail.getId();
        }
        return 0;
    }

    public Boolean updateCategory(CategoryDetail categoryDetail) {
        Long currentTime = System.currentTimeMillis();
        categoryDetail.setUpdateTime(currentTime);
        return categoryDetailMapper.updateByPrimaryKeySelective(categoryDetail) > 0;
    }

    public CategoryDetail queryCategoryById(Integer id) {
        return categoryDetailMapper.selectByPrimaryKey(id);
    }
    public CategoryDetail queryCategoryByName(String name) {
        return categoryDetailMapper.selectByCategoryName(name);
    }


    public List<CategoryDetail> queryCategoryByRelatedId(Integer relateCategoryId) {
        List<CategoryDetail> categoryDetails = categoryDetailMapper.queryAllByRelateCategoryId(relateCategoryId);
        return categoryDetails;
    }

    public List<CategoryDetail> queryCategoryByType(Integer type) {
        List<CategoryDetail> categoryDetails = categoryDetailMapper.queryAllByType(type);
        return categoryDetails;
    }

}
