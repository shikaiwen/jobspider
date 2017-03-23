package com.kevin.com.kevin.jobdispatcher;

import com.kevin.com.kevin.spider.webmagic.MainHandler;
import com.kevin.entity.BlogMember;
import com.kevin.service.MemberService;
import com.kevin.service.ServiceFactory;

import java.util.List;

/**
 * Created by kaiwen on 23/03/2017.
 */
public class UpdateCheckerThread extends BaseThread {


    MemberService memberService = ServiceFactory.getService(MemberService.class);

    boolean newMsg = false;

    @Override
    public void controle(String action) {


    }

    @Override
    public void run() {

        while(true){

            if(newMsg){

            }


            MainHandler mainHandler = new MainHandler();

            //获取10个用户去执行更新操作
            List<BlogMember> memberToUpdate = memberService.getMemberToUpdate(10);

            memberToUpdate.forEach(member->{
                mainHandler.doUpdateJobByMember(member);
            });


        }


    }
}
