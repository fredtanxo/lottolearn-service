package xo.fredtan.lottolearn.domain.user;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import xo.fredtan.lottolearn.domain.audit.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Entity
@Table(name = "user")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class User extends AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @NotBlank(message = "必须提供用户昵称")
    private String nickname;
    private Boolean gender;
    private String avatar;
    private String description;
    private Boolean status;
}
