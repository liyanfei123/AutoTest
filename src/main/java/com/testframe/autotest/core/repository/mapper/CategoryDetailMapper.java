package com.testframe.autotest.core.repository.mapper;

import com.testframe.autotest.core.meta.po.CategoryDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CategoryDetail record);

    int insertSelective(CategoryDetail record);

    CategoryDetail selectByPrimaryKey(Integer id);

    CategoryDetail selectByCategoryName(String categoryName);

    List<CategoryDetail> queryAllByRelateCategoryId(Integer relateCategoryId);

    List<CategoryDetail> queryAllByType(Integer type);

    int updateByPrimaryKeySelective(CategoryDetail record);

    int updateByPrimaryKey(CategoryDetail record);
}