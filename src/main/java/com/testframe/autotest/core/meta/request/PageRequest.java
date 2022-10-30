package com.testframe.autotest.core.meta.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 *
 * @date:2021/08/01 21:38
 * @author: lyf
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    private int page = 1;
    private int pageSize = 10;
    private Long lastId = -1L;

}
