package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import xo.fredtan.lottolearn.domain.audit.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@Table(name = "term")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Term extends AbstractEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @NotBlank(message = "必须提供学期名称")
    private String name;
    @NotNull(message = "必须提供学期开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date from;
    @NotNull(message = "必须提供学期结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date to;
    private Boolean status;
}
