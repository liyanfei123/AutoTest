package com.testframe.autotest.cache.meta.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDetailCo {

    private Integer categoryId;

    private String categoryName;

    private Long createTime;

}
