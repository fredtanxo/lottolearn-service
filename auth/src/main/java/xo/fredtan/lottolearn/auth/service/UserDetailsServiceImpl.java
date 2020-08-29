package xo.fredtan.lottolearn.auth.service;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import xo.fredtan.lottolearn.api.user.constants.UserAccountType;
import xo.fredtan.lottolearn.api.user.service.UserAccountService;
import xo.fredtan.lottolearn.auth.domain.JwtUser;
import xo.fredtan.lottolearn.domain.user.response.UserOfAccount;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @DubboReference(version = "0.0.1")
    private UserAccountService userAccountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserOfAccount userOfAccount = userAccountService.findUserByAccountAndType(username, UserAccountType.PASSWORD);
        return JwtUser.of(userOfAccount);
    }
}
