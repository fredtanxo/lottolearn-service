package xo.fredtan.lottolearn.domain.user;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "必须提供账户ID")
    @Column(name = "user_id")
    private Long userId;
    @NotNull(message = "必须提供角色ID")
    @Column(name = "role_id")
    private Long roleId;
}
