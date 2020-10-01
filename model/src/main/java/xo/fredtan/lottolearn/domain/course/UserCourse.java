package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "user_course")
public class UserCourse implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "course_id")
    private Long courseId;
    @Column(name = "is_teacher")
    private Boolean isTeacher;
    @Column(name = "enroll_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date enrollDate;
    private Boolean status;
}
