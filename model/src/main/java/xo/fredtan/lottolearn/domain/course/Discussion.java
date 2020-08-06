package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "discussion")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Discussion {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @Column(name = "course_id")
    private String courseId;
    private String content;
    @Column(name = "reply_to")
    private String replyTo;
    @Column(name = "poster_id")
    private String posterId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "pub_date")
    private Date pubDate;
    private Integer votes;
}
