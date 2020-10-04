package xo.fredtan.lottolearn.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.user.controller.UserAccountControllerApi;
import xo.fredtan.lottolearn.api.user.service.UserAccountService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.UserAccount;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserAccountController implements UserAccountControllerApi {
    private final UserAccountService userAccountService;

    @Override
    @GetMapping("/current/all")
    public QueryResponseData<UserAccount> findCurrentAccounts() {
        return userAccountService.findCurrentAccounts();
    }

    @Override
    @GetMapping("/id/{accountId}")
    public UniqueQueryResponseData<UserAccount> findUserAccountById(@PathVariable Long accountId) {
        return userAccountService.findUserAccountById(accountId);
    }

    @Override
    @PostMapping("/new")
    public BasicResponseData addUserAccount(@RequestBody @Valid UserAccount userAccount) {
        return userAccountService.addUserAccount(userAccount);
    }

    @Override
    @PutMapping("/id/{accountId}")
    public BasicResponseData updateAccount(@PathVariable Long accountId, @RequestBody @Valid UserAccount userAccount) {
        return userAccountService.updateAccount(accountId, userAccount);
    }

    @Override
    @DeleteMapping("/id/{accountId}")
    public BasicResponseData closeAccount(@PathVariable Long accountId) {
        return userAccountService.closeAccount(accountId);
    }
}
