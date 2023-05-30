package com.testframe.autotest.core.meta.vo.common;

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
public class PageVO {

    private int pageNum;

    private int pageSize;

    private boolean hasNext;

    private Long lastId;

    private int totalPage;

    private Long totalCount;

//    public PageVO(PageRequest pageParam) {
//        this.pageNum = pageParam.getPage();
//        this.pageSize = pageParam.getPageSize();
//        this.lastId = pageParam.getLastId();
//    }
}
