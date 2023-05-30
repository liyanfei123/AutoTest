package com.testframe.autotest.meta.query;

import com.testframe.autotest.core.meta.request.PageQry;
import lombok.Data;

@Data
public class CategorySceneQry {

    private Integer categoryId;

    private PageQry pageQry;

}
