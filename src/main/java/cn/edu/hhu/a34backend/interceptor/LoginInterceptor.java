package cn.edu.hhu.a34backend.interceptor;

import cn.edu.hhu.a34backend.utils.HTTPUtils;
import cn.edu.hhu.a34backend.vo.Result;
import cn.edu.hhu.a34backend.vo.StatusEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${setting.auth-server.address}")
    private String authServerAddress;

    @Value("${setting.auth-server.auth-key}")
    private String authServerAuthKey;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        //handler可能是资源
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

        String jsonResult = HTTPUtils.httpGet(authServerAddress + "/verification/verifyToken?token="+token, authServerAuthKey);

        // json转为Result
        Result result = JSONObject.parseObject(jsonResult, Result.class);
        if(!result.isSuccess()){
            Result returnResult = Result.fail(StatusEnum.NO_LOGIN);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(returnResult));
            return false;
        }

        // 放行
        return true;
    }
}
