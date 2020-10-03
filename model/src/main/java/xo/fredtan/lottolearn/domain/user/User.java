package xo.fredtan.lottolearn.domain.user;

import lombok.Data;
import xo.fredtan.lottolearn.domain.audit.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User extends AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "必须提供用户昵称")
    private String nickname;
    private Boolean gender;
    private String avatar;
    private String description;
    private Boolean status;

    @Transient
    private List<Long> roleIds;
}
