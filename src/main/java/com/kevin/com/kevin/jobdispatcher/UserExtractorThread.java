package com.kevin.com.kevin.jobdispatcher;

import com.kevin.constant.Const;
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
 * 从评论中获取新用户,有去重功能，不会保存已经存在的用户
 */
public class UserExtractorThread extends BaseThread{


    MemberService memberService = ServiceFactory.getService(MemberService.class);
    ArticleService articleService = ServiceFactory.getService(ArticleService.class);
    CommentService commentService = ServiceFactory.getService(CommentService.class);

    @Override
    public void run() {

        while(true){
            List<CsdnComment> commentToExtractUser = commentService.getCommentToExtractUser(10);

            List <BlogMember> memberList = new ArrayList<>();
            commentToExtractUser.forEach((comment)->{

                String userName = comment.getUserName();

                BlogMember m = new BlogMember();
                m.setFetchType("1");
                m.setSource("1");
                m.setUsername(userName);
                m.setBlogUrl(Const.CSDN_BLOG_DOMAIN + userName);
                m.setCreateDate(new Date());
                m.setUpdateDate(new Date());
                memberList.add(m);
            });

            boolean b = memberService.saveBlogMember(memberList);
        }


    }

}
