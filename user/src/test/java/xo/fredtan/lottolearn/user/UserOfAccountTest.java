package xo.fredtan.lottolearn.user;

import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import xo.fredtan.lottolearn.api.user.constants.UserAccountType;
import xo.fredtan.lottolearn.api.user.service.UserAccountService;
import xo.fredtan.lottolearn.domain.user.response.UserOfAccount;

@SpringBootTest
public class UserOfAccountTest {
    @DubboReference(version = "0.0.1")
    private UserAccountService userAccountService;

    @Test
    public void testGetUserOfAccount() {
        String account = "fred";
        UserAccountType type = UserAccountType.PASSWORD;
        UserOfAccount userByAccountAndType = userAccountService.findUserByAccountAndType(account, type);
        System.out.println(userByAccountAndType);
    }
}
