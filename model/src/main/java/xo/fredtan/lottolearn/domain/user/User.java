package xo.fredtan.lottolearn.domain.user;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import xo.fredtan.lottolearn.domain.audit.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "user")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class User extends AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    private String nickname;
    private Boolean gender;
    private String avatar;
    private String description;
    private Boolean status;
}
