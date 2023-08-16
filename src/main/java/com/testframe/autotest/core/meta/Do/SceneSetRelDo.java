package com.testframe.autotest.core.meta.Do;

import lombok.Data;

@Data
public class SceneSetRelDo {

    private Long relId;

    public Long setId;

    public Long sceneId;

    public Long stepId;

    public Integer type;

    public String extInfo;

    public Integer status;

    public Integer sort;

    public Long updateTime;
}
