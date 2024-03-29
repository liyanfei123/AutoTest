package com.testframe.autotest.domain.category.impl;

import com.testframe.autotest.core.enums.CategoryTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.CategoryDetailDo;
import com.testframe.autotest.core.meta.Do.CategorySceneDo;
import com.testframe.autotest.core.meta.convertor.CategoryDetailConverter;
import com.testframe.autotest.core.meta.po.CategoryScene;
import com.testframe.autotest.core.repository.CategoryDetailRepository;
import com.testframe.autotest.core.repository.CategorySceneRepository;
import com.testframe.autotest.domain.category.CategorySceneDomain;
import com.testframe.autotest.domain.category.CategoryDomain;
import com.testframe.autotest.meta.bo.CategoryDetailBo;
import com.testframe.autotest.meta.bo.CategorySceneBo;
import com.testframe.autotest.meta.dto.category.CategoryDto;
import com.testframe.autotest.meta.query.CategoryQry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class CategoryDomainImpl implements CategoryDomain {

    @Autowired
    private CategoryDetailConverter categoryDetailConverter;

    @Autowired
    private CategoryDetailRepository categoryDetailRepository;

    @Autowired
    private CategorySceneRepository categorySceneRepository;

    @Autowired
    private CategorySceneDomain categorySceneDomain;

    @Override
    public List<CategoryDetailBo> listCategory() {
        List<CategoryDetailBo> categoryDetailBos = new ArrayList<>();
        // 查询所有一级类目
        CategoryQry categoryQry = new CategoryQry(null, null, null, CategoryTypeEnum.PRIMARY.getType());
        List<CategoryDetailDo> categoryDetailDos = categoryDetailRepository.queryCategory(categoryQry);
        for (CategoryDetailDo categoryDetailDo : categoryDetailDos) {
            CategoryDetailBo categoryDetailBo = new CategoryDetailBo();
            categoryDetailBo.setCategoryId(categoryDetailDo.getCategoryId());
            categoryDetailBo.setCategoryName(categoryDetailDo.getCategoryName());
            categoryDetailBo.setCategories(categoryIn(categoryDetailDo.getCategoryId()));
            categoryDetailBos.add(categoryDetailBo);
        }
        return categoryDetailBos;
    }

    private List<CategoryDetailBo> categoryIn(Integer categoryId) {
        List<CategoryDetailBo> categoryDetailBos = new ArrayList<>();
        // 查询当前类目下的子类目
        CategoryQry categoryQry = new CategoryQry(null, null, categoryId, null);
        List<CategoryDetailDo> categoryDetailDos = categoryDetailRepository.queryCategory(categoryQry);
        if (categoryDetailDos.isEmpty()) {
            // 无子目录
            return Collections.EMPTY_LIST;
        }
        for (CategoryDetailDo categoryDetailDo : categoryDetailDos) {
            CategoryDetailBo categoryDetailBo = new CategoryDetailBo();
            categoryDetailBo.setCategoryId(categoryDetailDo.getCategoryId());
            categoryDetailBo.setCategoryName(categoryDetailDo.getCategoryName());
            categoryDetailBo.setCategories(categoryIn(categoryDetailDo.getCategoryId()));
            categoryDetailBos.add(categoryDetailBo);
        }
        return categoryDetailBos;
    }



    @Override
    public Integer updateCategory(CategoryDto categoryDto) {
        try {
            CategoryQry categoryQry = new CategoryQry(null, categoryDto.getCategoryName(), null, null);
            List<CategoryDetailDo> existNameCategoryDetailDos = categoryDetailRepository.
                    queryCategory(categoryQry);
            if (categoryDto.getCategoryId() > 0 && existNameCategoryDetailDos.size() > 0 &&
                    existNameCategoryDetailDos.get(0).getType() == CategoryTypeEnum.PRIMARY.getType()) {
                throw new AutoTestException("存在同名一级类目");
            }
            if (categoryDto.getCategoryId() > 0) {
                // 更新
                categoryQry = new CategoryQry(categoryDto.getCategoryId(), null, null, null);
                List<CategoryDetailDo> existCategoryDetailDos = categoryDetailRepository.queryCategory(categoryQry);
                if (existCategoryDetailDos.size() == 0) {
                    throw new AutoTestException("当前类目id错误");
                }
                CategoryDetailDo updateCategoryDetailDo = existCategoryDetailDos.get(0);
                if (updateCategoryDetailDo.getIsDelete() == 1) {
                    throw new AutoTestException("已删除场景不支持更新");
                }
                if (categoryDto.getRelatedCategoryId() == 0) {
                    categoryDto.setRelatedCategoryId(updateCategoryDetailDo.getRelateCategoryId());
                } else {
                    categoryQry = new CategoryQry(null, null, categoryDto.getRelatedCategoryId(), null);
                    List<CategoryDetailDo> relatedCategoryDetailDos = categoryDetailRepository.queryCategory(categoryQry);
                    if (relatedCategoryDetailDos.isEmpty()) {
                        throw new AutoTestException("当前关联类目id错误");
                    }
                }
                updateCategoryDetailDo = categoryDetailConverter.DtoToDo(updateCategoryDetailDo, categoryDto);
                categoryDetailRepository.updateCategory(updateCategoryDetailDo);
            } else {
                // 新增
                CategoryDetailDo newCategoryDetailDo = categoryDetailConverter.DtoToDo(categoryDto);
                if (categoryDto.getRelatedCategoryId() > 0) {
                    // 新增子目录
                    categoryQry = new CategoryQry(null, null, categoryDto.getRelatedCategoryId(), null);
                    List<CategoryDetailDo> relatedCategoryDetailDos = categoryDetailRepository.queryCategory(categoryQry);
                    if (relatedCategoryDetailDos.isEmpty()) {
                        throw new AutoTestException("当前关联类目id错误");
                    }
                }
                newCategoryDetailDo.setType(categoryDto.getRelatedCategoryId() > 0 ? CategoryTypeEnum.MULTI.getType() :
                        CategoryTypeEnum.PRIMARY.getType());
                newCategoryDetailDo.setIsDelete(0);
                return categoryDetailRepository.saveCategory(newCategoryDetailDo);
            }
        } catch (Exception e) {
            log.error("");
            e.printStackTrace();
            throw new AutoTestException(e.getMessage());
        }
        return 0;
    }

    @Override
    public Boolean deleteCategory(Integer categoryId) {
        // 删除，同时判断当前类目下是否有关联的场景
        CategoryDetailDo categoryDetailDo = getCategory(categoryId);
        if (categoryDetailDo == null) {
            throw new AutoTestException("当前目录不存在");
        }
        Boolean sceneExist = categorySceneDomain.sceneInCategory(categoryId);
        if (sceneExist) {
            throw new AutoTestException("当前目录下有关联的场景，不允许删除");
        }
        categoryDetailDo.setIsDelete(1);
        categoryDetailRepository.updateCategory(categoryDetailDo);
        return true;
    }

    @Override
    public CategoryDetailBo getCategoryById(Integer categoryId) {
        CategoryDetailDo categoryDetailDo = getCategory(categoryId);
        CategoryDetailBo categoryDetailBo = new CategoryDetailBo();
        categoryDetailBo.setCategoryId(categoryDetailDo.getCategoryId());
        categoryDetailBo.setCategoryName(categoryDetailDo.getCategoryName());
        categoryDetailBo.setCategories(null);
        return categoryDetailBo;
    }

    private CategoryDetailDo getCategory(Integer categoryId) {
        CategoryQry categoryQry = new CategoryQry(categoryId, null, null, null);
        List<CategoryDetailDo> categoryDetailDos = categoryDetailRepository.queryCategory(categoryQry);
        if (categoryDetailDos.size() == 0) {
            return null;
        }
        CategoryDetailDo categoryDetailDo = categoryDetailDos.get(0);
        return categoryDetailDo;
    }


}
