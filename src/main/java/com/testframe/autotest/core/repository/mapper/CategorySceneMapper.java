package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.CategoryScene;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategorySceneMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CategoryScene record);

    int insertSelective(CategoryScene record);

    CategoryScene selectByPrimaryKey(Long id);

    List<CategoryScene> selectByCategoryId(Integer categoryId);

    CategoryScene selectBySceneId(Long sceneId);

    int updateByPrimaryKeySelective(CategoryScene record);

    int updateByPrimaryKey(CategoryScene record);
}