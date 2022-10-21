CREATE DATABASE IF NOT EXISTS AUTOTEST DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_chinese_ci;

CREATE TABLE AUTO_TEST_SCENE_DETAIL(
       id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
       sceneName VARCHAR(128) NOT NULL  DEFAULT '' COMMENT '场景名称' ,
       sceneDesc varchar(10000) NOT NULL DEFAULT '' COMMENT '场景描述' ,
       type INT NOT NULL  DEFAULT 1 COMMENT '测试类型 1:UI测试,2:接口测试' ,
       url VARCHAR(1024) NOT NULL  DEFAULT '' COMMENT '访问地址' ,
       waitType INT NOT NULL  DEFAULT 1 COMMENT '等待方式 1:显式等待,2:隐式等待' ,
       waitTime INT NOT NULL  DEFAULT 0 COMMENT '等待时间 超时等待时间' ,
       isDelete INT NOT NULL  DEFAULT 0 COMMENT '是否被删除 0:未删除,1:已删除' ,
       createBy BIGINT NOT NULL  DEFAULT 0 COMMENT '创建人 uid' ,
       createTime BIGINT NOT NULL  DEFAULT 0 COMMENT '创建时间' ,
       updateTime BIGINT NOT NULL  DEFAULT 0 COMMENT '更新时间' ,
       PRIMARY KEY (id)
) COMMENT = '场景详情表';

CREATE TABLE AUTO_TEST_STEP_DETAIL(
      id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
      stepName VARCHAR(1024) NOT NULL DEFAULT '' COMMENT '步骤名称' ,
      stepInfo varchar(10000) NOT NULL DEFAULT '' COMMENT '步骤执行信息' ,
      createTime BIGINT NOT NULL  DEFAULT 0 COMMENT '创建时间' ,
      updateTime BIGINT NOT NULL  DEFAULT 0 COMMENT '更新时间' ,
      PRIMARY KEY (id)
) COMMENT = '步骤详情表 ';


CREATE TABLE AUTO_TEST_SCENE_STEP(
     id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
     sceneId BIGINT NOT NULL  DEFAULT 0 COMMENT '场景id' ,
     stepId BIGINT NOT NULL  DEFAULT 0 COMMENT '步骤id' ,
     status INT NOT NULL  DEFAULT 0 COMMENT '状态 0:未启用,1:启用' ,
     isDelete INT NOT NULL  DEFAULT 0 COMMENT '是否删除 0:未删除,1:已删除' ,
     createTime BIGINT NOT NULL  DEFAULT 0 COMMENT '创建时间' ,
     updateTime BIGINT NOT NULL  DEFAULT 0 COMMENT '更新时间' ,
     PRIMARY KEY (id)
) COMMENT = '场景步骤关联表';


CREATE TABLE AUTO_TEST_STEP_ORDER(
     id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
     sceneId BIGINT NOT NULL  DEFAULT 0 COMMENT '场景id' ,
     orderList varchar(10000) NOT NULL DEFAULT '[]' COMMENT '步骤执行顺序' ,
     type VARCHAR(32) NOT NULL DEFAULT 0 COMMENT '类型 1:执行前的顺序，2.执行时的顺序' ,
     createTime BIGINT NOT NULL DEFAULT 0 COMMENT '创建时间' ,
     updateTime BIGINT NOT NULL DEFAULT 0 COMMENT '更新时间' ,
     PRIMARY KEY (id)
) COMMENT = '场景步骤执行顺序表';


CREATE TABLE AUTO_TEST_SCENE_EXECUTE_RECORD(
   id BIGINT NOT NULL AUTO_INCREMENT  COMMENT '执行记录id' ,
   sceneId BIGINT NOT NULL  DEFAULT 0 COMMENT '场景id' ,
   sceneName VARCHAR(128) NOT NULL  DEFAULT '' COMMENT '场景名称 当前执行时的场景名称' ,
   url VARCHAR(1024) NOT NULL  DEFAULT '' COMMENT '访问地址 执行时所定义的访问地址' ,
   waitType INT NOT NULL  DEFAULT 1 COMMENT '等待方式 执行时所定义的超时等待方式' ,
   waitTime INT NOT NULL  DEFAULT 0 COMMENT '等待时间 执行时所定义的超时等待时间' ,
   status INT NOT NULL  DEFAULT 0 COMMENT '执行结果 0:成功,1:失败,2:中止' ,
   orderList VARCHAR(10000) NOT NULL  DEFAULT '[]' COMMENT '步骤执行顺序' ,
   extInfo VARCHAR(10000) NOT NULL  DEFAULT '' COMMENT '失败原因' ,
   createTime BIGINT NOT NULL  DEFAULT 0 COMMENT '创建时间' ,
   PRIMARY KEY (id)
) COMMENT = '场景执行记录表 ';

CREATE TABLE AUTO_TEST_STEP_EXECUTE_RECORD(
  id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
  recordId BIGINT NOT NULL DEFAULT 0  COMMENT '执行记录id' ,
  stepId BIGINT NOT NULL DEFAULT 0  COMMENT '步骤id' ,
  reason VARCHAR(10000) NOT NULL DEFAULT ''  COMMENT '失败原因' ,
  status INT NOT NULL DEFAULT 0  COMMENT '执行结果 0:成功，1:失败，2:跳过，3:中止' ,
  createTime BIGINT NOT NULL  DEFAULT 0 COMMENT '创建时间' ,
  PRIMARY KEY (id)
) COMMENT = '步骤执行记录表 ';

