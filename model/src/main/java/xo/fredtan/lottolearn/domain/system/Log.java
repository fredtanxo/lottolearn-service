package xo.fredtan.lottolearn.domain.system;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "log")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class Log implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    private String operation;
    private String method;
    private String args;
    @Column(name = "operator_id")
    private String operatorId;
    @Column(name = "operator_name")
    private String operatorName;
    private String ip;
    private Date time;
}
