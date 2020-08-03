package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "chapter")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Chapter {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @Column(name = "course_id")
    private String courseId;
    private String name;
    private Integer lasts;
    @Column(name = "pub_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pubDate;
}
