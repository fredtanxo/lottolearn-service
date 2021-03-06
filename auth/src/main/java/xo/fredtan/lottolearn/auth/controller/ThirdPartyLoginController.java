package xo.fredtan.lottolearn.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import xo.fredtan.lottolearn.api.auth.constants.AuthConstants;
import xo.fredtan.lottolearn.api.auth.controller.ThirdPartyControllerApi;
import xo.fredtan.lottolearn.auth.service.ThirdPartyLoginServiceImpl;
import xo.fredtan.lottolearn.auth.util.TokenResponseUtils;
import xo.fredtan.lottolearn.common.constant.LotToLearnConstants;
import xo.fredtan.lottolearn.domain.auth.JwtPair;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ThirdPartyLoginController implements ThirdPartyControllerApi {
    private final ThirdPartyLoginServiceImpl thirdPartyLoginService;

    /**
     * 处理第三方登录，登录成功后跳转到首页
     */
    @GetMapping("/login/third-party")
    public String thirdPartyLogin(HttpServletResponse response,
                                  @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
                                  @AuthenticationPrincipal OAuth2User oAuth2User) {
        JwtPair jwtPair = thirdPartyLoginService.findOrCreateUserToken(authorizedClient, oAuth2User);

        Cookie cookie = new Cookie(AuthConstants.ACCESS_TOKEN_KEY, jwtPair.getAccessToken().getToken());
        cookie.setPath(AuthConstants.TOKEN_COOKIE_PATH);
        cookie.setDomain(AuthConstants.ACCESS_TOKEN_COOKIE_DOMAIN);
        response.addCookie(cookie);

        TokenResponseUtils.setRefreshToken(response, jwtPair.getRefreshToken().getToken());

        return String.format("redirect:%s", LotToLearnConstants.HOME_PAGE);
    }
}
