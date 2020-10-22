package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "course_rating")
public class CourseRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long courseId;
    private Long userId;
    private Integer rating;
    private String comment;
    @Column(name = "rate_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date rateDate;

    @Transient
    private String userNickname;
    @Transient
    private String userAvatar;
}
