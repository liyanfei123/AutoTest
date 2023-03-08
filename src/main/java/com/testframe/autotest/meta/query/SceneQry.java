package com.testframe.autotest.meta.query;

import com.testframe.autotest.core.meta.request.PageQry;
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
public class SceneQry  {

    private String sceneName;

    private Integer status;

    // 搜索的类目id
    private Integer categoryId;

    private PageQry pageQry;

}
