package com.example.board.toyboard.resolver;

import com.example.board.toyboard.Entity.UserType;
import com.example.board.toyboard.session.SessionConst;
import com.example.board.toyboard.session.UserSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
@Slf4j
@RequiredArgsConstructor
public class AuthResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        return parameter.getParameterType().equals(UserSession.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {


        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new RuntimeException("권한이 없습니다.");
        }

        log.info("resolve 닉네임={}",session.getAttribute(SessionConst.NICKNAME));
        log.info("resolve 타입={}",session.getAttribute(SessionConst.USER_TYPE));
        log.info("resolve 유저아이디={}",session.getAttribute(SessionConst.USER_ID));

        return new UserSession(
                (Long) session.getAttribute(SessionConst.USER_ID),
                (String) session.getAttribute(SessionConst.NICKNAME),
                (UserType) session.getAttribute(SessionConst.USER_TYPE)
        );
    }
}
