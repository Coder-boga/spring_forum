package com.example.forum.services.impl;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.dao.ArticleMapper;
import com.example.forum.dao.ArticleReplyMapper;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Article;
import com.example.forum.model.ArticleReply;
import com.example.forum.model.User;
import com.example.forum.services.IArticleReplyService;
import com.example.forum.services.IArticleService;
import com.example.forum.services.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ArticleReplyServiceImpl implements IArticleReplyService {
    @Autowired
    private ArticleReplyMapper articleReplyMapper;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IUserService userService;

    @Override
    public void create(ArticleReply articleReply) {
        if (articleReply == null || articleReply.getArticleId() == null
            || articleReply.getPostUserId() == null || !StringUtils.hasLength(articleReply.getContent())) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        articleReply.setReplyId(null);
        articleReply.setReplyUserId(null);
        articleReply.setLikeCount(0);
        articleReply.setCreateTime(new Date());
        articleReply.setUpdateTime(new Date());

        User user = userService.selectById(articleReply.getPostUserId());
        if (user == null || user.getId() == null || user.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        int row = articleReplyMapper.insertSelective(articleReply);
        if (row != 1) {
            log.warn(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        articleService.addOneReplyCountById(articleReply.getArticleId());
        log.info("回复成功， article id = " + articleReply.getArticleId() + ", user id = " + articleReply.getPostUserId());
    }

    @Override
    public List<ArticleReply> selectByArticleId(Long articleId) {
        if (articleId == null || articleId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        Article article = articleService.selectById(articleId);
        if (article == null || article.getState() == 1 ) {
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }
        if (article.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }

        List<ArticleReply> articleReplies = articleReplyMapper.selectByArticleId(articleId);
        if (articleReplies == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        return articleReplies;
    }


}
