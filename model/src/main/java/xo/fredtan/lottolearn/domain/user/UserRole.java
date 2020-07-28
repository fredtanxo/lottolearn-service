package xo.fredtan.lottolearn.domain.user;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_role")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class UserRole {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "role_id")
    private String roleId;
}
