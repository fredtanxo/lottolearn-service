package xo.fredtan.lottolearn.domain.audit;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public class AbstractEntity {
    @Column(name = "created_by", updatable = false)
    public String createdBy;
    @Column(name = "created_at", updatable = false)
    public Date createdAt;
    @Column(name = "updated_by")
    public String updatedBy;
    @Column(name = "updated_at")
    public Date updatedAt;
}
