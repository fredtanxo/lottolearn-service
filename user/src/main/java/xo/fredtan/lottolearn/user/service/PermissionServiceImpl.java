package xo.fredtan.lottolearn.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xo.fredtan.lottolearn.api.user.service.PermissionService;
import xo.fredtan.lottolearn.common.model.response.UniqueQueryResponseData;
import xo.fredtan.lottolearn.domain.user.response.PermissionCodeSet;
import xo.fredtan.lottolearn.user.dao.MenuMapper;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PermissionServiceImpl implements PermissionService {
    private final MenuMapper menuMapper;

    @Override
    public UniqueQueryResponseData<PermissionCodeSet> findUserPermissionCodes(Long userId) {
        List<String> permissions = menuMapper.selectUserPermissions(userId);
        return UniqueQueryResponseData.ok(new PermissionCodeSet(new HashSet<>(permissions)));
    }
}
