package com.example.forum.services;

import com.example.forum.model.Board;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IBoardService {
    /**
     * 查询板块
     * @param num
     * @return
     */
    List<Board> selectByNum(Integer num);

    /**
     * 更新当前板块的发帖数 + 1
     * @param id
     */
    void addOneArticleCountById(Long id);

    /**
     * 根据板块Id查询
     * @param id
     * @return
     */
    Board selectById(Long id);

    /**
     * 板块中的帖子数量 - 1
     * @param id
     */
    void subOneArticleCountById(Long id);

}
