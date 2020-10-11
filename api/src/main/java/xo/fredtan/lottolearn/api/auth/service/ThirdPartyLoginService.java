package xo.fredtan.lottolearn.api.auth.service;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import xo.fredtan.lottolearn.domain.auth.JwtPair;

public interface ThirdPartyLoginService {
    JwtPair findOrCreateUserToken(OAuth2AuthorizedClient authorizedClient, OAuth2User oAuth2User);
}
