package com.testframe.autotest.core.meta.vo.common;

import com.testframe.autotest.core.meta.request.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 *
 * @date:2021/07/31 23:08
 * @author: lyf
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageVO {

    private int pageNum;

    private int pageSize;

    private Long lastId;

    public PageVO(PageRequest pageParam) {
        this.pageNum = pageParam.getPage();
        this.pageSize = pageParam.getPageSize();
        this.lastId = pageParam.getLastId();
    }
}
