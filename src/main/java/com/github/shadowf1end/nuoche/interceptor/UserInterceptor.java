package com.github.shadowf1end.nuoche.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.shadowf1end.nuoche.common.singleton.ObjectMapperSingleton;
import com.github.shadowf1end.nuoche.common.util.JwtUtil;
import com.github.shadowf1end.nuoche.common.util.ResultUtil;
import com.github.shadowf1end.nuoche.common.vo.Result;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author Sun
 * @date 2018/5/31
 */
@Slf4j
@Component
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("utf-8");
        String jwt = request.getHeader("Authorization");
        //token不存在
        if (jwt == null || jwt.isEmpty()) {
            responseMessage(response, response.getWriter(), new ResultUtil<>().setErrorMsg(502, "登录过期请重新登录"));
            return false;
        } else {
            try {
                Claims claims = JwtUtil.parseJwt(jwt);
                request.setAttribute("userId", claims.getSubject());
                return true;
            } catch (Exception e) {
                log.warn(e.toString());
                responseMessage(response, response.getWriter(), new ResultUtil<>().setErrorMsg(502, "登录过期请重新登录"));
                return false;
            }
        }
    }

    private void responseMessage(HttpServletResponse response, PrintWriter out, Result<Object> result) throws JsonProcessingException {
        response.setContentType("application/json; charset=utf-8");
        String json = ObjectMapperSingleton.getInstance().writeValueAsString(result);
        out.print(json);
        out.flush();
        out.close();
    }
}
