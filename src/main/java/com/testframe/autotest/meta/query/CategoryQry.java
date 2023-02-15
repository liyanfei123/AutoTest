package com.testframe.autotest.meta.query;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryQry {

    Integer categoryId;

    String categoryName;

    Integer relatedCategoryId;

    Integer type = null;
}
