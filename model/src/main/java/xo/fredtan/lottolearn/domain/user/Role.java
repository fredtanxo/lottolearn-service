package xo.fredtan.lottolearn.domain.user;

import lombok.Data;
import xo.fredtan.lottolearn.domain.audit.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "role")
public class Role extends AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "必须提供角色名称")
    private String name;
    @NotBlank(message = "必须提供角色代码")
    private String code;
    private String description;
    private Boolean status;

    @Transient
    private List<Long> menuIds;
}
