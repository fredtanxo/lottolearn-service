package xo.fredtan.lottolearn.domain.user;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import xo.fredtan.lottolearn.domain.audit.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "role")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Role extends AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    private String name;
    private String description;
    private Boolean status;
}
