package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.SignRecord;

import java.util.List;

public interface SignRecordRepository extends JpaRepository<SignRecord, String> {
    SignRecord findByUserIdAndSignId(String userId, String signId);

    List<SignRecord> findBySignIdOrderBySignTimeDesc(String signId);
}
