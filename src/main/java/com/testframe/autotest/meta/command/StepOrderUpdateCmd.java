package com.testframe.autotest.meta.command;

import lombok.Data;

import java.util.List;

@Data
public class StepOrderUpdateCmd {

    private Long sceneId;

    private List<Long> orders;
}
