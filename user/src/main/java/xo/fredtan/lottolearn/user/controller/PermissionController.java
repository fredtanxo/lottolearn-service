package xo.fredtan.lottolearn.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xo.fredtan.lottolearn.api.user.controller.PermissionControllerApi;
import xo.fredtan.lottolearn.api.user.service.PermissionService;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.response.PermissionCodeSet;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PermissionController implements PermissionControllerApi {
    private final PermissionService permissionService;

    @Override
    @GetMapping("/user/{userId}")
    public UniqueQueryResponseData<PermissionCodeSet> findUserPermissionCodes(@PathVariable Long userId) {
        return permissionService.findUserPermissionCodes(userId);
    }
}
