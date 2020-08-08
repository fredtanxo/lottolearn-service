package xo.fredtan.lottolearn.auth.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import xo.fredtan.lottolearn.domain.user.response.UserOfAccount;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Setter
public class JwtUser implements UserDetails {
    @Getter
    private String userId;
    private String username;
    private String password;
    private Boolean enabled;
    private Collection<? extends GrantedAuthority> authorities;

    public static JwtUser of(UserOfAccount userOfAccount) {
        JwtUser jwtUser = new JwtUser();

        if (Objects.nonNull(userOfAccount)) {
            jwtUser.setUserId(userOfAccount.getId());
            jwtUser.setUsername(userOfAccount.getUserAccount().getAccount());
            jwtUser.setPassword(userOfAccount.getUserAccount().getCredential());
            jwtUser.setEnabled(userOfAccount.getStatus());

            List<GrantedAuthority> authorities = new ArrayList<>();
            userOfAccount.getRoleList()
                    .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode())));
            userOfAccount.getMenuList()
                    .forEach(menu -> authorities.add(new SimpleGrantedAuthority(menu.getCode())));
            jwtUser.setAuthorities(authorities);
        }

        return jwtUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Objects.isNull(this.enabled) ? false : this.enabled;
    }
}
