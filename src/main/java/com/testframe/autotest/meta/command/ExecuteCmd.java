package com.testframe.autotest.meta.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCmd {

    private Long setId;

    private Long sceneId;

    private Integer browserType;
}
