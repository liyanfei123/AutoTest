package com.testframe.autotest.core.meta.Do;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySceneDo {

    private Long id;

    private Integer categoryId;

    private Long stepId;

    private Long sceneId;

    private Long setId;

    private Long createTime;

    private Integer isDelete;
}
