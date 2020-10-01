package xo.fredtan.lottolearn.course.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import xo.fredtan.lottolearn.domain.course.SignRecord;

import java.util.List;

public interface SignRecordRepository extends JpaRepository<SignRecord, Long> {
    SignRecord findByUserIdAndSignId(Long userId, Long signId);

    List<SignRecord> findBySignIdOrderBySignTimeDesc(Long signId);
}
