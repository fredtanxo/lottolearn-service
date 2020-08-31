package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signDate;
}
