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

    private List<SceneSimpleInfo> scenes;

    private PageVO pageVO;

}
