package com.testframe.autotest.controller;


import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.vo.common.http.HttpResult;
import com.testframe.autotest.domain.category.CategoryDomain;
import com.testframe.autotest.meta.bo.CategoryDetailBo;
import com.testframe.autotest.meta.bo.CategorySceneBo;
import com.testframe.autotest.meta.command.SceneCategoryCmd;
import com.testframe.autotest.meta.dto.sceneSet.ExeSetDto;
import com.testframe.autotest.meta.query.CategorySceneQry;
import com.testframe.autotest.meta.vo.SceneListVO;
import com.testframe.autotest.service.SceneCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/autotest/category")
public class CategoryController {

    @Autowired
    private SceneCategoryService sceneCategoryService;

    @Autowired
    private CategoryDomain categoryDomain;

    @GetMapping("/list")
    public HttpResult<CategorySceneBo> listCategory() {
        try {
            List<CategoryDetailBo>  categoryDetailBos = categoryDomain.listCategory();
            return HttpResult.ok(categoryDetailBos);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    // 仅可更新类目信息，不可更新关联
    @PostMapping("/update")
    public HttpResult<Long> updateCategory(@RequestBody SceneCategoryCmd sceneCategoryCmd) {
        try {
            Integer categoryId = sceneCategoryService.updateSceneCategory(sceneCategoryCmd);
            return HttpResult.ok(categoryId);
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    // 删除类目
    @GetMapping("/delete")
    public HttpResult<Boolean> deleteCategory(@RequestParam Integer categoryId) {
        try {
            sceneCategoryService.deleteSceneCategory(categoryId);
            return HttpResult.ok();
        } catch (AutoTestException e) {
            return HttpResult.error(e.getMessage());
        }
    }

    // TODO: 2023/5/29 查询当前目录下的场景，需要有分页逻辑
    @PostMapping("/scenes")
    public HttpResult<SceneListVO> scenes(@RequestBody CategorySceneQry categorySceneQry) {
        SceneListVO sceneListVO = sceneCategoryService.queryScenesByCategoryId(categorySceneQry);
        return HttpResult.ok(sceneListVO);
    }


    /**
     * 查询当前目录下的所有执行集，不需要分页
     * @return
     */
    @GetMapping("/sets")
    public HttpResult<Object> sets(@RequestParam Integer categoryId) {
        List<ExeSetDto> exeSetDtos = sceneCategoryService.querySetsByCategoryId(categoryId);
        return HttpResult.ok(exeSetDtos);
    }
}
