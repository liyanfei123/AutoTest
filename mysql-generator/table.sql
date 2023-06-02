CREATE DATABASE IF NOT EXISTS AUTOTEST DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_chinese_ci;

CREATE TABLE AUTO_TEST_SCENE_DETAIL(
       id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'id' ,
       sceneName VARCHAR(128) NOT NULL  DEFAULT '' COMMENT '场景名称' ,
       sceneDesc varchar(10000) NOT NULL DEFAULT '' COMMENT '场景描述' ,
       type INT NULL  DEFAULT 1 COMMENT '测试类型 1:UI测试,2:接口测试' ,
       url VARCHAR(1024) NULL  DEFAULT '' COMMENT '访问地址' ,
       waitType INT NULL  DEFAULT 1 COMMENT '等待方式 1:显式等待,2:隐式等待' ,
       waitTime INT NULL  DEFAULT 0 COMMENT '等待时间 超时等待时间' ,
       isDelete INT NULL  DEFAULT 0 COMMENT '是否被删除 0:未删除,1:已删除' ,
       createBy BIGINT NULL  DEFAULT 0 COMMENT '创建人 uid' ,
       createTime BIGINT NULL  DEFAULT 0 COMMENT '创建时间' ,
       updateTime BIGINT NULL  DEFAULT 0 COMMENT '更新时间' ,
       PRIMARY KEY (id)
) COMMENT = '场景详情表';

CREATE TABLE AUTO_TEST_STEP_DETAIL(
      id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
      stepName VARCHAR(1024) NULL DEFAULT '' COMMENT '步骤名称' ,
      sceneId BIGINT NULL  DEFAULT 0 COMMENT '子场景id' ,
      stepInfo varchar(10000) NULL DEFAULT '' COMMENT '步骤执行信息' ,
      createTime BIGINT NULL  DEFAULT 0 COMMENT '创建时间' ,
      updateTime BIGINT NULL  DEFAULT 0 COMMENT '更新时间' ,
      PRIMARY KEY (id)
) COMMENT = '步骤详情表 ';


CREATE TABLE AUTO_TEST_SCENE_STEP(
     id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
     sceneId BIGINT NOT NULL  DEFAULT 0 COMMENT '场景id' ,
     stepId BIGINT NOT NULL  DEFAULT 0 COMMENT '步骤id' ,
     status INT NULL  DEFAULT 0 COMMENT '状态 0:未启用,1:启用' ,
     type INT NULL  DEFAULT 0 COMMENT '步骤类型 1:单步骤,2:子场景' ,
     isDelete INT NULL  DEFAULT 0 COMMENT '是否删除 0:未删除,1:已删除' ,
     createTime BIGINT NULL  DEFAULT 0 COMMENT '创建时间' ,
     updateTime BIGINT NULL  DEFAULT 0 COMMENT '更新时间' ,
     PRIMARY KEY (id)
) COMMENT = '场景步骤关联表';


-- todo 增加isDelete字段代表全部删除
CREATE TABLE AUTO_TEST_STEP_ORDER(
     id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
     sceneId BIGINT NOT NULL  DEFAULT 0 COMMENT '场景id' ,
     recordId BIGINT NOT NULL  DEFAULT 0 COMMENT '场景执行记录id' ,
     orderList varchar(10000) NULL DEFAULT '[]' COMMENT '步骤执行顺序' ,
     type VARCHAR(32) NULL DEFAULT 0 COMMENT '类型 1:执行前的顺序，2.执行时的顺序' ,
     createTime BIGINT NULL DEFAULT 0 COMMENT '创建时间' ,
     updateTime BIGINT NULL DEFAULT 0 COMMENT '更新时间' ,
     PRIMARY KEY (id)
) COMMENT = '场景步骤执行顺序表';

CREATE TABLE AUTO_TEST_SET_EXECUTE_RECORD(
  id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
  setId BIGINT NOT NULL DEFAULT 0  COMMENT '执行集id' ,
  setName VARCHAR(1024) NULL DEFAULT '' COMMENT '执行集名称',
  status INT NULL DEFAULT 0  COMMENT '执行集执行结果 0:成功，1:失败' ,
  createTime BIGINT NULL  DEFAULT 0 COMMENT '创建时间' ,
  PRIMARY KEY (id)
) COMMENT = '执行集执行记录表 ';

-- todo 添加updateTime
CREATE TABLE AUTO_TEST_SCENE_EXECUTE_RECORD(
   id BIGINT NOT NULL AUTO_INCREMENT  COMMENT '执行记录id' ,
   setRecordId BIGINT NOT NULL AUTO_INCREMENT  COMMENT '执行集记录id 0:独立执行 >0:执行集执行' ,
   sceneId BIGINT NOT NULL  DEFAULT 0 COMMENT '场景id' ,
   sceneName VARCHAR(128) NOT NULL  DEFAULT '' COMMENT '场景名称 当前执行时的场景名称' ,
   url VARCHAR(1024) NULL  DEFAULT '' COMMENT '访问地址 执行时所定义的访问地址' ,
   waitType INT NULL  DEFAULT 1 COMMENT '等待方式 执行时所定义的超时等待方式' ,
   waitTime INT NULL  DEFAULT 0 COMMENT '等待时间 执行时所定义的超时等待时间' ,
   status INT NULL  DEFAULT 0 COMMENT '执行结果 0:成功,1:失败,2:中止' ,
   type INT NULL  DEFAULT 0 COMMENT '执行类型 1:单独执行,2:子场景执行' ,
   orderList VARCHAR(10000) NULL  DEFAULT '[]' COMMENT '步骤执行顺序' ,
   extInfo VARCHAR(10000) NULL  DEFAULT '' COMMENT '失败原因' ,
   createTime BIGINT NULL  DEFAULT 0 COMMENT '创建时间' ,
   PRIMARY KEY (id)
) COMMENT = '场景执行记录表 ';

alter table AUTO_TEST_SCENE_EXECUTE_RECORD ADD COLUMN `setRecordId` BIGINT NOT NULL DEFAULT 0 COMMENT '执行集记录id 0:独立执行 >0:执行集执行' after `id`;

CREATE TABLE AUTO_TEST_STEP_EXECUTE_RECORD(
  id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
  recordId BIGINT NOT NULL DEFAULT 0  COMMENT '场景执行记录id' ,
  stepId BIGINT NOT NULL DEFAULT 0  COMMENT '步骤id' ,
  sceneRecordId BIGINT NOT NULL DEFAULT 0  COMMENT '子场景执行记录id' ,
  stepName VARCHAR(1024) NULL DEFAULT '' COMMENT '步骤名称',
  reason VARCHAR(10000) NULL DEFAULT ''  COMMENT '失败原因' ,
  status INT NULL DEFAULT 0  COMMENT '执行结果 0:成功，1:失败，2:跳过，3:中止' ,
  createTime BIGINT NULL  DEFAULT 0 COMMENT '创建时间' ,
  PRIMARY KEY (id)
) COMMENT = '步骤执行记录表 ';

CREATE TABLE AUTO_TEST_SET(
  id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
  setName VARCHAR(1024) NOT NULL DEFAULT ''  COMMENT '集合名称' ,
  status INT NULL DEFAULT 0  COMMENT '集合状态 0:未开启 1:开启' ,
  isDelete INT NULL  DEFAULT 0 COMMENT '是否删除 0:未删除,1:已删除' ,
  createTime BIGINT NULL  DEFAULT 0 COMMENT '创建时间' ,
  updateTime BIGINT NULL  DEFAULT 0 COMMENT '更新时间' ,
  PRIMARY KEY (id)
) COMMENT = '执行集表 ';


CREATE TABLE AUTO_TEST_SCENE_SET_REL(
  id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
  setId BIGINT NOT NULL DEFAULT 0  COMMENT '集合id' ,
  stepId INT NULL DEFAULT 0  COMMENT '步骤id' ,
  sceneId INT NULL DEFAULT 0  COMMENT '场景id' ,
  sort INT NULL DEFAULT 0 COMMENT '排序' ,
  type INT NULL DEFAULT 0 COMMENT '是否删除 0:场景,1:单步骤' ,
  isDelete INT NULL  DEFAULT 0 COMMENT '是否删除 0:未删除,1:已删除' ,
  status INT NULL DEFAULT 0  COMMENT '执行状态 0:未开启 1:开启' ,
  createTime BIGINT NULL  DEFAULT 0 COMMENT '创建时间' ,
  updateTime BIGINT NULL  DEFAULT 0 COMMENT '更新时间' ,
  PRIMARY KEY (id)
) COMMENT = '执行集关联场景表 ';

alter table AUTO_TEST_STEP_EXECUTE_RECORD add column `stepName` VARCHAR(1024) NOT NULL DEFAULT ''  COMMENT '步骤名称' after `stepId`;
-- 步骤执行记录表中需增加步骤名称，用于返回执行结果记录

alter table AUTO_TEST_STEP_ORDER ADD COLUMN `recordId` BIGINT NOT NULL  DEFAULT 0 COMMENT '场景执行记录id' after `sceneId`;

-- 增加子场景执行功能
alter table AUTO_TEST_STEP_DETAIL ADD COLUMN `sceneId` BIGINT NOT NULL DEFAULT 0 COMMENT '子场景id' after `stepName`;
alter table AUTO_TEST_SCENE_STEP ADD COLUMN `type` INT NOT NULL DEFAULT 1 COMMENT '步骤类型 1:单步骤,2:子场景' after `stepId`;
alter table AUTO_TEST_STEP_EXECUTE_RECORD ADD COLUMN `sceneRecordId` BIGINT NOT NULL DEFAULT 0 COMMENT '子场景执行记录id' after `stepId`;
alter table AUTO_TEST_SCENE_EXECUTE_RECORD ADD COLUMN `type` INT NOT NULL DEFAULT 1 COMMENT '执行类型 1:单独执行,2:子场景执行' after `status`;


CREATE TABLE AUTO_TEST_CATEGORY(
  id INT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
  categoryName VARCHAR(256) NOT NULL DEFAULT ''  COMMENT '类目名' ,
  relateCategoryId INT NULL DEFAULT 0  COMMENT '父目录id' ,
  type INT NULL DEFAULT 1  COMMENT '目录类型 1:一级目录，2:子目录' ,
  isDelete INT NULL  DEFAULT 0 COMMENT '是否删除 0:未删除,1:已删除' ,
  createTime BIGINT NULL  DEFAULT 0 COMMENT '创建时间' ,
  updateTime BIGINT NULL  DEFAULT 0 COMMENT '更新时间' ,
  PRIMARY KEY (id)
) COMMENT = '目录表 ';


CREATE TABLE AUTO_TEST_CATEGORY_SCENE(
  id BIGINT NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
  categoryId INT NULL DEFAULT 0  COMMENT '目录id' ,
  stepId BIGINT NULL DEFAULT 0  COMMENT '步骤id' ,
  sceneId BIGINT NULL DEFAULT 0  COMMENT '场景id' ,
  setId BIGINT NULL DEFAULT 0  COMMENT '执行集id' ,
  isDelete INT NULL  DEFAULT 0 COMMENT '是否删除 0:未删除,1:已删除' ,
  createTime BIGINT NULL  DEFAULT 0 COMMENT '创建时间' ,
  updateTime BIGINT NULL  DEFAULT 0 COMMENT '更新时间' ,
  PRIMARY KEY (id)
) COMMENT = '目录场景关联表 ';

alter table AUTO_TEST_CATEGORY_SCENE ADD COLUMN `stepId` BIGINT NULL DEFAULT 0  COMMENT '步骤id' after `categoryId`;
alter table AUTO_TEST_CATEGORY_SCENE ADD COLUMN `setId` BIGINT NULL DEFAULT 0  COMMENT '执行集id' after `sceneId`;
