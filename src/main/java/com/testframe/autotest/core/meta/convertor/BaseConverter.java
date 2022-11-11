package com.testframe.autotest.core.meta.convertor;

import com.testframe.autotest.core.meta.po.StepOrder;
import com.testframe.autotest.meta.bo.SceneStepOrder;

public abstract class BaseConverter {
    public abstract Object toPo(Object param);
}
