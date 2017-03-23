package com.kevin.com.kevin.jobdispatcher;

import com.kevin.entity.BlogArticle;
import com.kevin.entity.BlogMember;
import com.kevin.entity.CsdnComment;
import com.kevin.service.ArticleService;
import com.kevin.service.CommentService;
import com.kevin.service.MemberService;
import com.kevin.service.ServiceFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kaiwen on 23/03/2017.
 */
public class NewUserHandlerThread extends BaseThread{


    MemberService memberService = ServiceFactory.getService(MemberService.class);
    ArticleService articleService = ServiceFactory.getService(ArticleService.class);
    CommentService commentService = ServiceFactory.getService(CommentService.class);

    @Override
    public void run() {

        List<BlogMember> newMember = memberService.getNewMember(5);

        List <BlogArticle> articleAll = new ArrayList<>(1000);
        List <CsdnComment> commentsAll = new ArrayList <>(5000);
        newMember.forEach((member)->{
            String username = member.getUsername();
            List <BlogArticle> userAllArticle = articleService.getAllArticleByUserName(username);

            userAllArticle.forEach((article)->{
                Integer articleId = article.getArticleId();

                if (article.getCommentCount() > 0) {
                    List <CsdnComment> comentList = commentService.getCommentListByArticleId(articleId);
                    comentList.forEach((c)->{c.setCreateDate(new Date());});
                    commentsAll.addAll(comentList);
                }

            });
            articleAll.addAll(userAllArticle);
        });

        articleService.saveArticle(articleAll);
        commentService.saveComment(commentsAll);

    }
}
