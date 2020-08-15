package xo.fredtan.lottolearn.api.auth.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.servlet.http.HttpServletResponse;

public interface ThirdPartyControllerApi {
    String thirdPartyLogin(HttpServletResponse response, OAuth2AuthorizedClient authorizedClient, OAuth2User oAuth2User);
}
