package xo.fredtan.lottolearn.domain.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "discussion")
public class Discussion implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chapter_id")
    private Long chapterId;
    private String content;
    @Column(name = "reply_to")
    private Long replyTo;
    @Column(name = "user_id")
    private Long userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "pub_date")
    private Date pubDate;
    private Long votes;
    private Long replies;
    private Long interactions;

    @Transient
    private String userNickname;
    @Transient
    private String userAvatar;
    @Transient
    private List<Discussion> replyList;
}
