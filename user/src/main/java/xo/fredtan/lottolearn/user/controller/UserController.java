package xo.fredtan.lottolearn.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xo.fredtan.lottolearn.api.user.controller.UserControllerApi;
import xo.fredtan.lottolearn.api.user.service.UserService;
import xo.fredtan.lottolearn.common.annotation.ValidatePagination;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.User;
import xo.fredtan.lottolearn.domain.user.request.ModifyUserRequest;
import xo.fredtan.lottolearn.domain.user.request.QueryUserRequest;
import xo.fredtan.lottolearn.domain.user.response.UserWithRoleIds;

import java.util.Objects;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController implements UserControllerApi {
    private final UserService userService;

    @Override
    @GetMapping("/all")
    @ValidatePagination
    public QueryResponseData<User> findAllUsers(Integer page, Integer size, QueryUserRequest queryUserRequest) {
        return userService.findAllUsers(page, size, queryUserRequest);
    }

    @Override
    @GetMapping("/id/{userId}")
    public UniqueQueryResponseData<UserWithRoleIds> findUserById(@PathVariable Long userId, Boolean withRoles) {
        if (Objects.isNull(withRoles) || !withRoles) {
            return userService.findUserById(userId);
        }
        return userService.findUserByIdWithRoleIds(userId);
    }

    @Override
    @GetMapping("/current")
    public UniqueQueryResponseData<User> findCurrentUser() {
        return userService.findCurrentUser();
    }

    @Override
    @PostMapping("/new")
    public BasicResponseData addUser(@RequestBody ModifyUserRequest modifyUserRequest) {
        return userService.addUser(modifyUserRequest);
    }

    @Override
    @PutMapping("/id/{userId}")
    public BasicResponseData updateUser(@PathVariable Long userId, @RequestBody ModifyUserRequest modifyUserRequest) {
        return userService.updateUser(userId, modifyUserRequest);
    }

    @Override
    @DeleteMapping("/id/{userId}")
    public BasicResponseData closeUser(@PathVariable Long userId) {
        return userService.closeUser(userId);
    }
}
