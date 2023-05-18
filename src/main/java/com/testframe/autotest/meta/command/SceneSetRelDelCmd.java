package com.testframe.autotest.meta.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SceneSetRelDelCmd {

    private Long setId;

    private Long stepId;

    private Long sceneId;

}
