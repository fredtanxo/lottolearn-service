package xo.fredtan.lottolearn.domain.course;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_course")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class UserCourse {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "course_id")
    private String courseId;
    @Column(name = "is_teacher")
    private Boolean isTeacher;
    private Boolean status;
}
