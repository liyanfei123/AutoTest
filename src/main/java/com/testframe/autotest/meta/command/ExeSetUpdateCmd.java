package com.testframe.autotest.meta.command;

import com.testframe.autotest.core.meta.Do.ExeSetDo;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ExeSetUpdateCmd extends ExeSetDo {

    private Integer categoryId;
}
