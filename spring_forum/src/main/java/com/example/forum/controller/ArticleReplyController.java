package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.config.AppConfig;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Article;
import com.example.forum.model.ArticleReply;
import com.example.forum.model.User;
import com.example.forum.services.IArticleReplyService;
import com.example.forum.services.IArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "回复接口")
@RequestMapping("/reply")
public class ArticleReplyController {

    @Autowired
    private IArticleReplyService articleReplyService;

    @Autowired
    private IArticleService articleService;

    @Operation(summary = "回复帖子")
    @PostMapping("/create")
    public AppResult create(HttpServletRequest request,
                            @Parameter(description = "帖子Id") @RequestParam("articleId") @NonNull Long articleId,
                            @Parameter(description = "回复内容") @RequestParam("content") @NonNull String content) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        if (user == null || user.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        if (user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        Article article = articleService.selectById(articleId);
        if (article == null || article.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_NOT_EXISTS.toString());
            return AppResult.failed(ResultCode.FAILED_NOT_EXISTS);
        }

        if (article.getState() == 1) {
            log.warn(ResultCode.FAILED_BOARD_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_BOARD_BANNED);
        }

        ArticleReply articleReply = new ArticleReply();
        articleReply.setArticleId(articleId);
        articleReply.setPostUserId(user.getId());
        articleReply.setContent(content);

        articleReplyService.create(articleReply);

        return AppResult.success();
    }

    @Operation(summary = "获取回复列表")
    @GetMapping("/getReplies")
    public AppResult getReplies(@Parameter(description = "帖子Id") @RequestParam("articleId") @NonNull Long articleId) {
        Article article = articleService.selectById(articleId);
        if (article == null || article.getState() == 1 ) {
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }
        if (article.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        List<ArticleReply> articleReplies = articleReplyService.selectByArticleId(articleId);
        return AppResult.success(articleReplies);

    }

}
