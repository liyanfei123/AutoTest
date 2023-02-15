package com.testframe.autotest.meta.bo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
//查询数据库返回的数据，简称BO, 返回给前端的数据，简称VO。
// 不会直接暴露给接口的

// 类+子类
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDetailBo {

    // 目录id
    private Integer categoryId;

    // 目录名称
    private String categoryName;

    // 目录类型 CategoryTypeEnum
    private List<CategoryDetailBo> categories;

}
