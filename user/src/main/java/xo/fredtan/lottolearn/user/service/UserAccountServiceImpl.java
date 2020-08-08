package xo.fredtan.lottolearn.user.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import xo.fredtan.lottolearn.api.user.constant.UserAccountType;
import xo.fredtan.lottolearn.api.user.service.UserAccountService;
import xo.fredtan.lottolearn.domain.user.response.UserOfAccount;
import xo.fredtan.lottolearn.user.dao.UserAccountMapper;

@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountMapper userAccountMapper;

    @Override
    public UserOfAccount findUserByAccountAndType(String account, UserAccountType type) {
        return userAccountMapper.selectUserByAccountAndType(account, type.getType());
    }
}
