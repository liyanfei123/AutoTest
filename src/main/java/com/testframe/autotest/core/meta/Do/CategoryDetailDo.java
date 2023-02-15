package com.testframe.autotest.core.meta.Do;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDetailDo {

    private Integer categoryId;

    private String categoryName;

    private Integer relateCategoryId;

    private Integer type;

    private Integer isDelete;
}
