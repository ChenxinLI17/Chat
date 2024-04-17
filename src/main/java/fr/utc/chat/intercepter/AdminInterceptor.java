package fr.utc.chat.intercepter;

import fr.utc.chat.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AdminInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 在请求处理之前进行拦截，返回 true 表示继续进行处理，返回 false 则会停止请求处理
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null || !user.isAdmin()){
            //想拦截后加一个消息"请先登录"在前端渲染
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}
