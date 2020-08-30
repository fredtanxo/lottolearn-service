package xo.fredtan.lottolearn.domain.course;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "chapter_resource")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class ChapterResource implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @Column(name = "chapter_id")
    private String chapterId;
    @Column(name = "resource_id")
    private String resourceId;
    private Boolean status;
}
