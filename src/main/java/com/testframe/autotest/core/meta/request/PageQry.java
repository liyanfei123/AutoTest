package com.testframe.autotest.core.meta.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 *
 * @date:2022/10/30 16:37
 * @author: lyf
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQry {

    private int offset = 1;
    private int size = 10;
    private Long lastId = -1L;
    private Integer type;
}
