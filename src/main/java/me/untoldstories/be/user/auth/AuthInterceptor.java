package me.untoldstories.be.user.auth;

import me.untoldstories.be.user.auth.pojos.SignedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class AuthInterceptor implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
    private final TokenManager tokenManager = TokenManager.getInstance();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(request.getMethod().equals("OPTIONS")) return true; //for CORS

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        logger.info(request.getMethod() + " " + request.getRequestURL());

        boolean signInRequired = !handlerMethod.hasMethodAnnotation(SigninNotRequired.class);

        String token = request.getHeader("token");
        SignedInUser signedInUser = tokenManager.verifyTokenAndDecodeData(token);

        if (signedInUser == null && signInRequired) {
            addErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Sign in required to access this api");
            return false;
        }

        request.setAttribute("user", signedInUser);
        return true;
    }

    private void addErrorResponse(HttpServletResponse response, int statusCode, String message) {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        try {
            response.getWriter().printf("{\"message\":\"%s\"}", message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}