package com.testframe.autotest.meta.vo;

import com.testframe.autotest.core.meta.vo.common.PageVO;
import lombok.Data;

import java.util.List;

/**
 * Description:
 *
 * @date:2022/10/29 20:18
 * @author: lyf
 */
@Data
public class SceneListVO {

//    private PageVO pageVO;

    private List<SceneSimpleInfo> scenes;

    private Long lastId;

    private Boolean hasNext;

}
