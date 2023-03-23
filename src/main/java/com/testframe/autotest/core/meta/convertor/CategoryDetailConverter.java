package com.testframe.autotest.core.meta.convertor;


import com.testframe.autotest.cache.meta.co.CategoryDetailCo;
import com.testframe.autotest.core.meta.Do.CategoryDetailDo;
import com.testframe.autotest.core.meta.po.CategoryDetail;
import com.testframe.autotest.meta.dto.category.CategoryDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryDetailConverter {

    public CategoryDetail DoToPo(CategoryDetailDo categoryDetailDo) {
        CategoryDetail categoryDetail = new CategoryDetail();
        categoryDetail.setId(categoryDetailDo.getCategoryId());
        categoryDetail.setCategoryName(categoryDetailDo.getCategoryName());
        categoryDetail.setRelateCategoryId(categoryDetailDo.getRelateCategoryId());
        categoryDetail.setType(categoryDetailDo.getType());
        categoryDetail.setIsDelete(categoryDetailDo.getIsDelete());
        return categoryDetail;
    }

    public CategoryDetailDo PoToDo(CategoryDetail categoryDetail) {
        CategoryDetailDo categoryDetailDo = new CategoryDetailDo();
        categoryDetailDo.setCategoryId(categoryDetail.getId());
        categoryDetailDo.setCategoryName(categoryDetail.getCategoryName());
        categoryDetailDo.setRelateCategoryId(categoryDetail.getRelateCategoryId());
        categoryDetailDo.setType(categoryDetail.getType());
        categoryDetailDo.setCreateTime(categoryDetail.getCreateTime());
        categoryDetailDo.setIsDelete(categoryDetail.getIsDelete());
        return categoryDetailDo;
    }

    public CategoryDetailDo DtoToDo(CategoryDto categoryDto) {
        CategoryDetailDo categoryDetailDo = new CategoryDetailDo();
        categoryDetailDo.setCategoryId(categoryDto.getCategoryId());
        categoryDetailDo.setCategoryName(categoryDto.getCategoryName());
        categoryDetailDo.setRelateCategoryId(categoryDto.getRelatedCategoryId());
        return categoryDetailDo;
    }

    public CategoryDetailDo DtoToDo(CategoryDetailDo categoryDetailDo, CategoryDto categoryDto) {
        categoryDetailDo.setCategoryName(categoryDto.getCategoryName());
        categoryDetailDo.setRelateCategoryId(categoryDto.getRelatedCategoryId());
        return categoryDetailDo;
    }

    public CategoryDetailCo DoToCo(CategoryDetailDo categoryDetailDo) {
        CategoryDetailCo categoryDetailCo = new CategoryDetailCo();
        categoryDetailCo.setCategoryId(categoryDetailDo.getCategoryId());
        categoryDetailCo.setCategoryName(categoryDetailDo.getCategoryName());
        categoryDetailCo.setCreateTime(categoryDetailDo.getCreateTime());
        return categoryDetailCo;
    }
}
