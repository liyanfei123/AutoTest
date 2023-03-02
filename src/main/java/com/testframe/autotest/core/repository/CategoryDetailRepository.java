package com.testframe.autotest.core.repository;

import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.CategoryDetailDo;
import com.testframe.autotest.core.meta.convertor.CategoryDetailConverter;
import com.testframe.autotest.core.meta.po.CategoryDetail;
import com.testframe.autotest.core.repository.dao.CategoryDetailDao;
import com.testframe.autotest.meta.query.CategoryQry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CategoryDetailRepository {

    @Autowired
    private CategoryDetailDao categoryDetailDao;

    @Autowired
    private CategoryDetailConverter categoryDetailConverter;

    @Transactional(rollbackFor = Exception.class)
    public Integer saveCategory(CategoryDetailDo categoryDetailDo) {
        CategoryDetail categoryDetail = categoryDetailConverter.DoToPo(categoryDetailDo);
        Integer categoryId = categoryDetailDao.saveCategory(categoryDetail);
        return categoryId;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCategory(CategoryDetailDo categoryDetailDo) {
        CategoryDetail categoryDetail = categoryDetailConverter.DoToPo(categoryDetailDo);
        if (!categoryDetailDao.updateCategory(categoryDetail)) {
            throw new AutoTestException("类目更新失败");
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCategory(Integer categoryId) {
        CategoryDetail categoryDetail = categoryDetailDao.queryCategoryById(categoryId);
        categoryDetail.setIsDelete(1);
        if (!categoryDetailDao.updateCategory(categoryDetail)) {
            throw new AutoTestException("类目删除失败");
        }
        return true;
    }

    // 查询类目的综合方法，暂时不支持联合查询
    @Transactional
    public List<CategoryDetailDo> queryCategory(CategoryQry categoryQry) {
        CategoryDetail categoryDetail = new CategoryDetail();
        List<CategoryDetailDo> categoryDetailDos = new ArrayList<>();
        if (categoryQry.getRelatedCategoryId() != null || categoryQry.getType() != null) {
            List<CategoryDetail> categoryDetails = new ArrayList<>();
            if (categoryQry.getRelatedCategoryId() != null) {
                categoryDetails = categoryDetailDao.queryCategoryByRelatedId(categoryQry.getRelatedCategoryId());
            } else if (categoryQry.getType() != null) {
                categoryDetails = categoryDetailDao.queryCategoryByType(categoryQry.getType());
            }
            if (categoryDetails.isEmpty()) {
                return Collections.EMPTY_LIST;
            }
            categoryDetailDos = categoryDetails.stream().map(categoryDetailConverter::PoToDo).collect(Collectors.toList());
            return categoryDetailDos;
        }
        if (categoryQry.getCategoryId() != null) {
            categoryDetail = categoryDetailDao.queryCategoryById(categoryQry.getCategoryId());
        } else if (categoryQry.getCategoryName() != null) {
            categoryDetail = categoryDetailDao.queryCategoryByName(categoryQry.getCategoryName());
        }
        if (categoryDetail == null) {
            return Collections.EMPTY_LIST;
        }
        CategoryDetailDo categoryDetailDo = categoryDetailConverter.PoToDo(categoryDetail);
        categoryDetailDos.add(categoryDetailDo);
        return categoryDetailDos;
    }

}
