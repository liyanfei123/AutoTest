package com.testframe.autotest.service;

/**
 * Description:
 *
 * @date:2022/10/30 20:46
 * @author: lyf
 */
public interface CopyService {

    public Long sceneCopy(Long sceneId);

    public Long stepCopy(Long sceneId, Long stepId);

}
