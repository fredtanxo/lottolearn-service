package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "sign_record")
@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
public class SignRecord implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "sign_id")
    private String signId;
    @Column(name = "user_nickname")
    private String userNickname;
    @Column(name = "sign_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signTime;
    private Boolean success;
}
