package com.testframe.autotest.meta.query;

import com.testframe.autotest.core.meta.request.PageQry;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SceneSetRelQry {

    private Long setId;

    private Long sceneId;

    private Long stepId;

    private PageQry pageQry;

}
