package xo.fredtan.lottolearn.api.user.service;

import xo.fredtan.lottolearn.api.user.constants.UserAccountType;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.UserAccount;
import xo.fredtan.lottolearn.domain.user.response.UserOfAccount;

public interface UserAccountService {
    QueryResponseData<UserAccount> findAllUserAccount(Integer page, Integer size);

    UniqueQueryResponseData<UserAccount> findUserAccountById(Long accountId);

    QueryResponseData<UserAccount> findCurrentAccounts();

    BasicResponseData addUserAccount(UserAccount userAccount);

    BasicResponseData updateAccount(Long accountId, UserAccount userAccount);

    BasicResponseData closeAccount(Long accountId);

    UserOfAccount findUserByAccountAndType(String account, UserAccountType type);

    BasicResponseData createUserWithDefaultRole(UserOfAccount userOfAccount);
}
