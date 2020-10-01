package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "resource_library")
public class ResourceLibrary implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "course_id")
    private Long courseId;
    private String name;
    private String filename;
    private Long size;
    @Column(name = "mime_type")
    private String mimeType;
    private Long uploader;
    @Column(name = "local_path")
    private String localPath;
    @Column(name = "access_url")
    private String accessUrl;
    private Integer type;
    @Column(name = "upload_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadDate;
    private Integer status;
}
