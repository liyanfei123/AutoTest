package com.testframe.autotest.service;

import com.testframe.autotest.meta.vo.SceneExeRecordVo;
import com.testframe.autotest.meta.vo.SceneRecordListVo;

import java.util.List;

/**
 * Description:
 *
 * @date:2022/11/04 22:38
 * @author: lyf
 */
public interface SceneRecordService {

    SceneRecordListVo records(Long sceneId);

}
