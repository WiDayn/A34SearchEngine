package cn.edu.hhu.a34searchengine.interceptor;

import cn.edu.hhu.a34searchengine.util.HTTPUtil;
import cn.edu.hhu.a34searchengine.util.Timer;
import cn.edu.hhu.a34searchengine.vo.Result;
import cn.edu.hhu.a34searchengine.vo.StatusEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${settings.auth-server.url}")
    private String authServerURL;

    @Value("${settings.auth-server.auth-key}")
    private String authServerAuthKey;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        //handler可能是资源
        Timer timer=new Timer();
        if (!(handler instanceof HandlerMethod)){
            return true;
        }

        String token = httpServletRequest.getHeader("Authorization");

        if (token == null || token.isEmpty()){
            Result result = Result.fail(StatusEnum.NO_LOGIN);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }

        String jsonResult = HTTPUtil.httpGet(authServerURL + "?token="+token, authServerAuthKey);

        // json转为Result
        Result result = JSONObject.parseObject(jsonResult, Result.class);
        if(!result.isSuccess()){
            Result returnResult = Result.fail(StatusEnum.NO_LOGIN);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(returnResult));
            return false;
        }
        timer.stop();
        // 放行
        return true;
    }
}
