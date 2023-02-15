package com.testframe.autotest.domain.category;

import com.testframe.autotest.meta.bo.CategoryDetailBo;
import com.testframe.autotest.meta.dto.category.CategoryDto;

import java.util.List;

public interface CategoryDomain {

    public List<CategoryDetailBo> listCategory();

    public Integer updateCategory(CategoryDto categoryDto);

    public Boolean deleteCategory(Integer categoryId);

    public CategoryDetailBo getCategoryById(Integer categoryId);

}
