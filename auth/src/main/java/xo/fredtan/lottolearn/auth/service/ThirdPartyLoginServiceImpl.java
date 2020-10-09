package xo.fredtan.lottolearn.auth.service;

import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xo.fredtan.lottolearn.api.auth.constants.AuthConstants;
import xo.fredtan.lottolearn.api.auth.service.ThirdPartyLoginService;
import xo.fredtan.lottolearn.api.user.constants.UserAccountType;
import xo.fredtan.lottolearn.api.user.service.UserAccountService;
import xo.fredtan.lottolearn.auth.util.JwtUtil;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.CommonCode;
import xo.fredtan.lottolearn.domain.user.Menu;
import xo.fredtan.lottolearn.domain.user.Role;
import xo.fredtan.lottolearn.domain.user.UserAccount;
import xo.fredtan.lottolearn.domain.user.response.UserOfAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ThirdPartyLoginServiceImpl implements ThirdPartyLoginService {
    private final RSAKey rsaKey;

    @DubboReference(version = "0.0.1")
    private UserAccountService userAccountService;

    public String findOrCreateUserToken(OAuth2AuthorizedClient authorizedClient, OAuth2User oAuth2User) {
        String registrationId = authorizedClient.getClientRegistration().getRegistrationId();
        UserAccountType type = UserAccountType.valueOf(registrationId.toUpperCase());
        String account = authorizedClient.getPrincipalName();

        UserOfAccount userOfAccount = userAccountService.findUserByAccountAndType(account, type);
        if (Objects.isNull(userOfAccount)) { // 新用户
            UserOfAccount userPre = switch (type) {
                case GITHUB -> createUserOfAccountOfGitHub(authorizedClient, oAuth2User);
                case WEIBO -> createUserOfAccountOfWeibo(authorizedClient, oAuth2User);
                default -> null;
            };
            BasicResponseData response = userAccountService.createUserWithDefaultRole(userPre);
            if (Objects.isNull(response) || !response.getCode().equals(CommonCode.OK.getCode())) {
                return null;
            }
            userOfAccount = userAccountService.findUserByAccountAndType(account, type);
        }

        List<String> authorities = parseAuthorities(userOfAccount.getRoleList(), userOfAccount.getMenuList());

        Map<String, String> claims = Map.of(AuthConstants.TOKEN_CLAIM_KEY, String.join(" ", authorities),
                "nickname", userOfAccount.getNickname());

        return JwtUtil.issueRSAToken(
                rsaKey,
                AuthConstants.ISSUER,
                userOfAccount.getId(),
                claims,
                AuthConstants.EXPIRATION_OFFSET
        );
    }

    private UserOfAccount populateUserOfAccount(UserOfAccount userOfAccount, OAuth2AuthorizedClient client, UserAccountType type) {
        UserAccount userAccount = new UserAccount();
        userAccount.setAccount(client.getPrincipalName());
        userAccount.setType(type.getType());
        userAccount.setCredential(null);
        userAccount.setStatus(true);

        userOfAccount.setUserAccount(userAccount);
        return userOfAccount;
    }

    private UserOfAccount createUserOfAccountOfGitHub(OAuth2AuthorizedClient authorizedClient, OAuth2User oAuth2User) {
        UserOfAccount userOfAccount = new UserOfAccount();

        String nickname = oAuth2User.getAttribute("name");
        if (StringUtils.isEmpty(nickname)) {
            nickname = oAuth2User.getAttribute("login");
        }
        userOfAccount.setNickname(nickname);
        userOfAccount.setAvatar(oAuth2User.getAttribute("avatar_url"));
        userOfAccount.setDescription(oAuth2User.getAttribute("bio"));
        userOfAccount.setStatus(true);

        return populateUserOfAccount(userOfAccount, authorizedClient, UserAccountType.GITHUB);
    }

    private UserOfAccount createUserOfAccountOfWeibo(OAuth2AuthorizedClient authorizedClient, OAuth2User oAuth2User) {
        UserOfAccount userOfAccount = new UserOfAccount();

        userOfAccount.setNickname(oAuth2User.getAttribute("name"));
        userOfAccount.setAvatar(oAuth2User.getAttribute("avatar_hd"));
        userOfAccount.setGender("m".equals(oAuth2User.getAttribute("gender")));
        userOfAccount.setDescription(oAuth2User.getAttribute("description"));
        userOfAccount.setStatus(true);

        return populateUserOfAccount(userOfAccount, authorizedClient, UserAccountType.WEIBO);
    }

    private List<String> parseAuthorities(List<Role> roleList, List<Menu> menuList) {
        List<String> list = new ArrayList<>();

        List<String> roles = roleList.stream().map(role -> "ROLE_" + role.getCode()).collect(Collectors.toList());
        List<String> menus = menuList.stream().map(Menu::getCode).collect(Collectors.toList());
        list.addAll(roles);
        list.addAll(menus);

        return list;
    }
}
