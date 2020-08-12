package xo.fredtan.lottolearn.domain.course;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "sign")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Sign implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @Column(name = "course_id")
    private String courseId;
    private Long timeout;
    private Date signDate;
}
