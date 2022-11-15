package com.testframe.autotest.service;

import com.testframe.autotest.meta.vo.SceneRecordListVo;

/**
 * Description:
 *
 * @date:2022/11/04 22:38
 * @author: lyf
 */
public interface SceneRecordService {

    SceneRecordListVo records(Long sceneId);

    Long saveRecord(Long sceneId);

}
