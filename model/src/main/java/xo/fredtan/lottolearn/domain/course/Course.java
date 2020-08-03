package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "course")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Course {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    private String name;
    private String cover;
    private String description;
    private String code;
    @Column(name = "teacher_id")
    private String teacherId;
    @Column(name = "term_id")
    private String termId;
    private Integer credit;
    @Column(name = "pub_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pubDate;
    private Integer status;
}
