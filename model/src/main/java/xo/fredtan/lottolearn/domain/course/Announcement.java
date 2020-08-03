package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "announcement")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Announcement {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @Column(name = "course_id")
    private String courseId;
    private String title;
    private String content;
    private String publisher;
    @Column(name = "pub_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pubDate;
}
