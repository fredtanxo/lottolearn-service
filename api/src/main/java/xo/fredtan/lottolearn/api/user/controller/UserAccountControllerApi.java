package xo.fredtan.lottolearn.api.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.UserAccount;

@Api("用户账户管理")
public interface UserAccountControllerApi {
    @ApiOperation("查询当前用户所有账户")
    QueryResponseData<UserAccount> findCurrentAccounts();

    @ApiOperation("根据账户ID查询账户信息")
    UniqueQueryResponseData<UserAccount> findUserAccountById(Long accountId);

    @ApiOperation("创建账户")
    BasicResponseData addUserAccount(UserAccount userAccount);

    @ApiOperation("修改账户")
    BasicResponseData updateAccount(Long accountId, UserAccount userAccount);

    @ApiOperation("停用账户")
    BasicResponseData closeAccount(Long accountId);
}
