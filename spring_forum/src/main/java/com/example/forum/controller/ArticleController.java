package com.example.forum.controller;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.config.AppConfig;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Article;
import com.example.forum.model.Board;
import com.example.forum.model.User;
import com.example.forum.services.IArticleService;
import com.example.forum.services.IBoardService;
import com.example.forum.services.IUserService;
import com.example.forum.services.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.swing.plaf.BorderUIResource;
import java.awt.event.HierarchyBoundsAdapter;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@Tag(name = "帖子接口")
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IBoardService boardService;

    @Autowired
    private IUserService userService;

    @Operation(summary = "发布帖子")
    @PostMapping("/create")
    public AppResult create(HttpServletRequest request,
                            @Parameter(description = "板块Id") @RequestParam("boardId") @NonNull Long boardId,
                            @Parameter(description = "帖子标题") @RequestParam("title") @NonNull String title,
                            @Parameter(description = "帖子正文") @RequestParam("content") @NonNull String content)  {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        if (user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        Board board = boardService.selectById(boardId);
        if (board == null || board.getDeleteState() != 0) {
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString());
            return AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS);
        }
        if (board.getState() != 0) {
            log.warn(ResultCode.FAILED_BOARD_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_BOARD_BANNED);
        }

        Article article = new Article();
        article.setUserId(user.getId());
        article.setBoardId(boardId);
        article.setTitle(title);
        article.setContent(content);

        articleService.create(article);
        User sessionUser = userService.selectById(user.getId());
        session.setAttribute(AppConfig.USER_SESSION, sessionUser);

        return AppResult.success();
    }

    @Operation(summary = "获取帖子列表")
    @GetMapping("/getAllByBoardId")
    public AppResult<List<Article>> getAllByBoardId(@Parameter(description = "板块Id") @RequestParam(value = "boardId", required = false) Long boardId) {
        List<Article> articles = null;
        if (boardId == null) {
            articles = articleService.selectAll();
        } else {
            articles = articleService.selectAllByBoardId(boardId);
        }

        if (articles == null) {
            articles = new ArrayList<>();
        }

        return AppResult.success(articles);
    }

    @Operation(summary = "获取贴子详情")
    @GetMapping("/getDetails")
    public AppResult<Article> getDetails(HttpServletRequest request,
                                @Parameter(description = "帖子Id") @RequestParam("id") @NonNull Long id) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        Article article = articleService.selectDetailById(id);
        if (article == null) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }

        if (user.getId() == article.getUserId()) {
            article.setOwn(true);
        }
        return AppResult.success(article);
    }

    @Operation(summary = "修改帖子")
    @PostMapping("/modify")
    public AppResult modify(HttpServletRequest request,
                            @Parameter(description = "帖子Id") @RequestParam("id") @NonNull Long id,
                            @Parameter(description = "帖子标题") @RequestParam("title") @NonNull String title,
                            @Parameter(description = "帖子正文") @RequestParam("content") @NonNull String content) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        if (user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        Article article = articleService.selectById(id);
        if (user.getId() != article.getUserId()) {
            log.warn(ResultCode.FAILED_FORBIDDEN.toString());
            return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
        }

        if (article.getState() == 1) {
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED);
        }

        articleService.modify(id, title, content);
        log.info("帖子修改成功，Article id = " + id + ", user id = " + article.getUserId());

        return AppResult.success();
    }

    @Operation(summary = "点赞帖子")
    @PostMapping("/thumbsUpById")
    public AppResult thumbsUpById(HttpServletRequest request,
                                  @Parameter(description = "帖子Id") @RequestParam("id") @NonNull Long id) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        if (user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED);
        }
        articleService.thumbsUpById(id);

        return AppResult.success();
    }

    @Operation(summary = "删除帖子")
    @PostMapping("/deleteById")
    public AppResult deleteById(HttpServletRequest request,
                                @Parameter(description = "帖子Id") @RequestParam("id") @NonNull Long id) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        if (user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            return AppResult.failed(ResultCode.FAILED_USER_BANNED);
        }

        Article article = articleService.selectById(id);

        if (user.getId() != article.getUserId()) {
            log.warn(ResultCode.FAILED_UNAUTHORIZED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_UNAUTHORIZED));
        }

        articleService.deleteById(id);
        User sessionUser = userService.selectById(user.getId());
        session.setAttribute(AppConfig.USER_SESSION, sessionUser);
        return AppResult.success();
    }

    @Operation(summary = "获取用户所有帖子")
    @GetMapping("/getAllByUserId")
    public AppResult<List<Article>> getAllByUserId(HttpServletRequest request,
                                                   @Parameter(name = "userId", description = "用户Id") @RequestParam(value = "userId",required = false)  Long userId) {
        if (userId == null) {
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute(AppConfig.USER_SESSION);
            userId = user.getId();
        }
        List<Article> articles = articleService.selectByUserId(userId);
        return AppResult.success(articles);
    }
}
