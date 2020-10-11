package xo.fredtan.lottolearn.auth.util;

import com.alibaba.fastjson.JSON;
import xo.fredtan.lottolearn.api.auth.constants.AuthConstants;
import xo.fredtan.lottolearn.domain.auth.JwtPair;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TokenResponseUtils {
    public static void respondJwtPair(HttpServletResponse response, JwtPair jwtPair) {
        setRefreshToken(response, jwtPair.getRefreshToken().getToken());

        Map<String, ? extends Serializable> content = Map.of(
                AuthConstants.ACCESS_TOKEN_KEY, jwtPair.getAccessToken().getToken(),
                AuthConstants.ACCESS_TOKEN_EXPIRATION_KEY, AuthConstants.ACCESS_TOKEN_EXPIRATION_OFFSET
        );

        String json = JSON.toJSONString(content);
        try (PrintWriter writer = response.getWriter()) {
            writer.write(json);
        } catch (IOException ignored) {
        }
    }

    /**
     * 将refresh_token保存在HttpOnly、Secure的Cookie中
     * @param response the {@link HttpServletResponse}
     * @param refreshToken {@code refresh_token}
     */
    public static void setRefreshToken(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(AuthConstants.REFRESH_TOKEN_KEY, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath(AuthConstants.TOKEN_COOKIE_PATH);
        cookie.setMaxAge((int) TimeUnit.SECONDS.convert(AuthConstants.REFRESH_TOKEN_EXPIRATION_OFFSET, TimeUnit.MILLISECONDS));
        response.addCookie(cookie);
    }

    /**
     * 清除Cookies
     * @param request the {@link HttpServletRequest}
     * @param response the {@link HttpServletResponse}
     */
    public static void clearCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (Objects.nonNull(cookies)) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                cookie.setPath("/");
                cookie.setValue(null);
                response.addCookie(cookie);
            }
        }
    }
}
