package xo.fredtan.lottolearn.domain.course;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Data
@Document(collation = "discussion")
public class Discussion {
    @Id
    private String id;
    private String courseId;
    private String content;
    private Discussion replyTo;
    private String posterId;
    private String posterName;
    private String posterAvatar;
    private Date pubDate;
    private Integer votes;
    private List<Discussion> replies;
}
