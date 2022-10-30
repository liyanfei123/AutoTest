package com.testframe.autotest.validator;

import com.testframe.autotest.core.enums.SceneStatusEnum;
import com.testframe.autotest.core.enums.SceneTypeEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.meta.query.SceneQry;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @date:2022/10/29 20:27
 * @author: lyf
 */
@Component
public class SceneQryValidator {

   private void checkParam(SceneQry sceneQry) {
       if (sceneQry.getSize() <= 0 || sceneQry.getSize() >= 30) {
           sceneQry.setSize(10);
       }
       if (sceneQry.getStatus() == null || !SceneStatusEnum.getTypes().contains(sceneQry.getStatus())) {
           throw new AutoTestException("场景状态筛选错误");
       }
   }


}
