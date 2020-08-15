package xo.fredtan.lottolearn.api.auth.service;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface ThirdPartyLoginService {
    String findOrCreateUserToken(OAuth2AuthorizedClient authorizedClient, OAuth2User oAuth2User);
}
