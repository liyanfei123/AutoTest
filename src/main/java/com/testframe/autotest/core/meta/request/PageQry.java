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

    private int page = 1;
    private long offset = 0;
    private int size = 10;
    private long lastId = -1L;
    // 1为按时间倒序排，2为正序
    private Integer orderType = 1;
}
