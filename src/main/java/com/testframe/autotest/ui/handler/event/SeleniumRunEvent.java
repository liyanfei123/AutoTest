package com.testframe.autotest.ui.handler.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 *
 * @date:2022/11/08 21:58
 * @author: lyf
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeleniumRunEvent {

    private Long sceneId;

}
