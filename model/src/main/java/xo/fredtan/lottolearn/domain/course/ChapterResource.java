package xo.fredtan.lottolearn.domain.course;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "chapter_resource")
public class ChapterResource implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chapter_id")
    private Long chapterId;
    @Column(name = "resource_id")
    private Long resourceId;
    private Boolean status;
}
