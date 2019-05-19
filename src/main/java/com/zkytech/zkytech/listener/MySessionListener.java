package com.zkytech.zkytech.listener;



import com.zkytech.zkytech.Utils;
import com.zkytech.zkytech.entity.Session;
import com.zkytech.zkytech.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Date;


@WebListener
public class MySessionListener implements HttpSessionListener, ServletRequestListener {

    public static int online = 0;



    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // 通过session统计网站在线人数

        se.getSession().setAttribute("new",true);
        online ++;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {

        online --;
    }


    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        String ip = Utils.getIpAddr((HttpServletRequest) sre.getServletRequest());
        HttpServletRequest hsre = (HttpServletRequest) sre.getServletRequest();
        HttpSession session = hsre.getSession();

        if(session.getAttribute("new").toString() == "true"){
            // 设置session的ip地址
            session.setAttribute("new","false");
            Session mySession = Session.of(new Date(session.getCreationTime()),ip);
            Utils utils = new Utils();
            utils.saveSession(
                    mySession
            );
            //TODO:通过ip分析地理位置
        }
    }
}
