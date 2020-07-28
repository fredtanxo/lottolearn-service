package xo.fredtan.lottolearn.domain.user;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import xo.fredtan.lottolearn.domain.audit.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "menu")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Menu extends AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column(name = "parent_id")
    private String parentId;
    private String name;
    private Boolean type;
    private String code;
    private String path;
    private String icon;
    private Long order;
    private Boolean status;
}
