package xo.fredtan.lottolearn.domain.course;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "term")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Term {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    private String name;
    private Date from;
    private Date to;
    private Boolean status;

    @Column(name = "created_by", updatable = false)
    public String createdBy;
    @Column(name = "created_at", updatable = false)
    public Date createdAt;
    @Column(name = "updated_by")
    public String updatedBy;
    @Column(name = "updated_at")
    public Date updatedAt;
}
