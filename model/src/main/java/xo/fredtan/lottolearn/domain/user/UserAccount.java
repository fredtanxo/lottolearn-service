package xo.fredtan.lottolearn.domain.user;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "user_account")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class UserAccount implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @Column(name = "user_id")
    private String userId;
    private String account;
    private Integer type;
    private String credential;
    private Boolean status;
}
