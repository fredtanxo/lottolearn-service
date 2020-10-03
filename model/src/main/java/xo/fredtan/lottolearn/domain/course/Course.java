package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "course")
public class Course implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "必须提供课程名称")
    private String name;
    private Boolean visibility;
    private String description;
    private String code;
    private String live;
    @Column(name = "teacher_id")
    private Long teacherId;
    @NotNull(message = "必须提供学期ID")
    @Column(name = "term_id")
    private Long termId;
    @NotNull(message = "必须提供学分")
    private Integer credit;
    @Column(name = "pub_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pubDate;
    private Integer status;

    @Transient
    private String teacherName;
    @Transient
    private String termName;
}
