package xo.fredtan.lottolearn.api.user.constants;

import java.util.Set;

public class RoleConstants {
    public static final String SUPERUSER_ROLE_CODE = "SUPERUSER";
    public static final String ADMIN_ROLE_CODE = "ADMIN";
    public static final String DEFAULT_ROLE_CODE = "USER";

    public static final Set<String> SYSTEM_ROLES = Set.of(SUPERUSER_ROLE_CODE, ADMIN_ROLE_CODE);
}
