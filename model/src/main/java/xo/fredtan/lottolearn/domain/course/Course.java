package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "course")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Course implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @NotBlank(message = "必须提供课程名称")
    private String name;
    private String cover;
    private String description;
    private String code;
    @Column(name = "teacher_id")
    private String teacherId;
    @NotBlank(message = "必须提供学期ID")
    @Column(name = "term_id")
    private String termId;
    @NotNull(message = "必须提供学分")
    private Integer credit;
    @Column(name = "pub_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pubDate;
    private Integer status;
}
