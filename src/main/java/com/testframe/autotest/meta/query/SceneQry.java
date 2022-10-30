package com.testframe.autotest.meta.query;

import com.testframe.autotest.core.meta.request.PageRequest;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 *
 * @date:2022/10/29 20:25
 * @author: lyf
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SceneQry {

    private String sceneName;

    private Long sceneId;

    private Integer status;

    private Integer page;

    private Integer size;

    private Long lastId;

}
