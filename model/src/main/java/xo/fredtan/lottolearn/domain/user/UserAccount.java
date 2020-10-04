package xo.fredtan.lottolearn.domain.user;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity
@Table(name = "user_account")
public class UserAccount implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "必须提供用户ID")
    @Column(name = "user_id")
    private Long userId;
    @NotNull(message = "必须提供账号")
    private String account;
    @NotNull(message = "必须提供账户类型")
    private Integer type;
    private String credential;
    private Boolean status;

    @Transient
    private String oldCredential;
}
