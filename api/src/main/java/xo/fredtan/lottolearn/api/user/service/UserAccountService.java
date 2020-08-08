package xo.fredtan.lottolearn.api.user.service;

import xo.fredtan.lottolearn.api.user.constant.UserAccountType;
import xo.fredtan.lottolearn.domain.user.response.UserOfAccount;

public interface UserAccountService {
    UserOfAccount findUserByAccountAndType(String account, UserAccountType type);
}
