package com.testframe.autotest.meta.bo;


import lombok.Data;

import java.util.List;

@Data
public class SceneBo {

    private Scene scene;

    private List<Step> steps;
}
