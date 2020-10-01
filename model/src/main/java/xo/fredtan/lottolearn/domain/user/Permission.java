package xo.fredtan.lottolearn.domain.user;

import lombok.Data;
import xo.fredtan.lottolearn.domain.audit.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Entity
@Table(name = "permission")
public class Permission extends AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "必须提供角色ID")
    private Long roleId;
    @NotBlank(message = "必须提供菜单ID")
    private Long menuId;
}
