package com.example.forum.dao;

import com.example.forum.model.Article;
import com.example.forum.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {
    int insert(Article row);

    int insertSelective(Article row);

    Article selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Article row);

    int updateByPrimaryKeyWithBLOBs(Article row);

    int updateByPrimaryKey(Article row);

    /**
     * 查询所有帖子
     * @return
     */
    List<Article> selectAll();

    /**
     * 根据板块Id查询帖子
     * @param boardId
     * @return
     */
    List<Article> selectAllByBoardId(@Param("boardId") Long boardId);

    /**
     * 根据帖子Id查看帖子详情
     * @param id
     * @return
     */
    Article selectDetailById(@Param("id") Long id);


    /**
     * 根据用户Id查询帖子
     * @param userId
     * @return
     */
    List<Article> selectByUserId(@Param("userId") Long userId);

}