package com.testframe.autotest.meta.dto.category;

import lombok.Data;

@Data
public class CategoryDto {

    private Integer categoryId;

    private String categoryName;

    private Integer relatedCategoryId;

}
