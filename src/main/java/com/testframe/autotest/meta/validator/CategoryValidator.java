package com.testframe.autotest.meta.validator;

import com.testframe.autotest.core.config.AutoTestConfig;
import com.testframe.autotest.core.enums.CategoryTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.Do.CategoryDetailDo;
import com.testframe.autotest.core.repository.CategoryDetailRepository;
import com.testframe.autotest.core.repository.CategorySceneRepository;
import com.testframe.autotest.meta.command.SceneCategoryCmd;
import com.testframe.autotest.meta.query.CategoryQry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@Slf4j
public class CategoryValidator {

    @Autowired
    private AutoTestConfig autoTestConfig;

    @Autowired
    private CategoryDetailRepository categoryDetailRepository;

    @Autowired
    private CategorySceneRepository categorySceneRepository;

    public void checkCategoryCreate(SceneCategoryCmd sceneCategoryCmd) {
        checkRelatedCategoryId(sceneCategoryCmd);
        checkCategoryName(sceneCategoryCmd);
    }

    public void checkCategoryUpdate(SceneCategoryCmd sceneCategoryCmd) {
        checkCategoryId(sceneCategoryCmd);
        checkRelatedCategoryId(sceneCategoryCmd);
        checkCategoryName(sceneCategoryCmd);
    }

    public void checkCategoryId(SceneCategoryCmd sceneCategoryCmd) {
        CategoryQry categoryQry = new CategoryQry(sceneCategoryCmd.getCategoryId(), null, null, null);
        List<CategoryDetailDo> existCategoryDetailDos = categoryDetailRepository.queryCategory(categoryQry);
        if (existCategoryDetailDos.size() == 0) {
            throw new AutoTestException("当前类目id错误");
        }
        CategoryDetailDo updateCategoryDetailDo = existCategoryDetailDos.get(0);
        if (updateCategoryDetailDo.getIsDelete() == 1) {
            throw new AutoTestException("已删除场景不支持更新");
        }

    }

    public void checkCategoryName(SceneCategoryCmd sceneCategoryCmd) {
        if (sceneCategoryCmd.getCategoryName() != null
                && sceneCategoryCmd.getCategoryName().length() > autoTestConfig.getCategoryNameLength()) {
            throw new AutoTestException("类目名称超过上限");
        }
        if (sceneCategoryCmd.getCategoryName() != null) {
            CategoryQry categoryQry = new CategoryQry(null, sceneCategoryCmd.getCategoryName(), null, null);
            List<CategoryDetailDo> categoryDetailDos = categoryDetailRepository.
                    queryCategory(categoryQry);
            if (categoryDetailDos.isEmpty()) {
                return;
            }
            for (CategoryDetailDo categoryDetailDo : categoryDetailDos) {
                if (categoryDetailDo.getCategoryId() == sceneCategoryCmd.getCategoryId() &&
                    categoryDetailDo.getRelateCategoryId() != sceneCategoryCmd.getRelatedCategoryId()) {
                    // 更新已有目录
                    throw new AutoTestException("存在同名类目");
                }
                if (!(sceneCategoryCmd.getCategoryId() > 0) && !(sceneCategoryCmd.getRelatedCategoryId() > 0)
                        && categoryDetailDo.getType() == CategoryTypeEnum.PRIMARY.getType()) {
                    // 新增一级目录
                    throw new AutoTestException("存在同名一级类目");
                }
                if (!(sceneCategoryCmd.getCategoryId() > 0)
                        && sceneCategoryCmd.getRelatedCategoryId() == categoryDetailDo.getRelateCategoryId()) {
                    // 新增多级目录
                    throw new AutoTestException("存在同名子类目");
                }
            }
        }
    }

    public void checkRelatedCategoryId(SceneCategoryCmd sceneCategoryCmd) {
        CategoryQry categoryQry = new CategoryQry(null, null, sceneCategoryCmd.getRelatedCategoryId(), null);
        List<CategoryDetailDo> relatedCategoryDetailDos = categoryDetailRepository.queryCategory(categoryQry);
        if (relatedCategoryDetailDos.isEmpty()) {
            throw new AutoTestException("当前关联类目id错误");
        }
    }

}
