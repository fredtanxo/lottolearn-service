package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "resource")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class ChapterResource {
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
    private String accessUrl;
    private Integer type;
    @Column(name = "upload_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadDate;
}
