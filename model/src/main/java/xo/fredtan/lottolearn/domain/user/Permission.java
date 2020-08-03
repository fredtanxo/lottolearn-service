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
@Table(name = "permission")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Permission extends AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @NotBlank(message = "必须提供角色ID")
    private String roleId;
    @NotBlank(message = "必须提供菜单ID")
    private String menuId;
}
