package com.example.forum.services;

import com.example.forum.model.Article;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IArticleService {

    /**
     * 发布帖子
     * @param article
     */
    @Transactional
    void create(Article article);

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
    List<Article> selectAllByBoardId(Long boardId);

    /**
     * 根据帖子Id查看帖子详情
     * @param id
     * @return
     */
    Article selectDetailById(Long id);

    /**
     * 修改帖子
     * @param id
     * @param title
     * @param content
     */
    public void modify(Long id, String title, String content);

    /**
     * 根据帖子id查询
     * @param id
     * @return
     */
    Article selectById(Long id);

    /**
     * 点赞帖子
     * @param id
     */
    void thumbsUpById(Long id);

    /**
     * 删除帖子
     * @param id
     */
    @Transactional
    void deleteById(Long id);

    /**
     * 根据帖子Id增加帖子回复数量 + 1
     * @param id
     */
    void addOneReplyCountById(Long id);


    /**
     * 根据用户Id查询帖子
     * @param userId
     * @return
     */
    List<Article> selectByUserId(Long userId);
}
