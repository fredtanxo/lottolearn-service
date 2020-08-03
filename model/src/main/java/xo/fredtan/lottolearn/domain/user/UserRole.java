package xo.fredtan.lottolearn.domain.user;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "user_role")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class UserRole {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @NotBlank(message = "必须提供账户ID")
    @Column(name = "user_id")
    private String userId;
    @NotBlank(message = "必须提供角色ID")
    @Column(name = "role_id")
    private String roleId;
}
