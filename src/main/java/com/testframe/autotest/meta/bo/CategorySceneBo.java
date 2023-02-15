package com.testframe.autotest.meta.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySceneBo {

    private Integer categoryId;

    private List<Long> sceneIds;

}
