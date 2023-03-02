package com.testframe.autotest.meta.query;

import com.testframe.autotest.core.meta.request.PageQry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordQry extends PageQry {

    private Integer type;

}
