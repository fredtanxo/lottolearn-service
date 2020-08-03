package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "media")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Media {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @Column(name = "chapter_id")
    private String chapterId;
    private String name;
    private String uploader;
    @Column(name = "local_path")
    private String localPath;
    @Column(name = "access_url")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date accessUrl;
}
