package xo.fredtan.lottolearn.system.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xo.fredtan.lottolearn.api.system.service.LogService;
import xo.fredtan.lottolearn.common.model.response.BasicResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResponseData;
import xo.fredtan.lottolearn.common.model.response.QueryResult;
import xo.fredtan.lottolearn.domain.system.Log;
import xo.fredtan.lottolearn.domain.system.request.QueryLogRequest;
import xo.fredtan.lottolearn.system.dao.LogRepository;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@DubboService(version = "0.0.1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LogServiceImpl implements LogService {
    private final LogRepository logRepository;

    @Override
    public QueryResponseData<Log> findAllLogs(Integer page, Integer size, QueryLogRequest queryLogRequest) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "time"));
        if (Objects.isNull(queryLogRequest.getTo())) {
            queryLogRequest.setTo(new Date());
        }

        Page<Log> logs = logRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(criteriaBuilder.lessThan(root.get("time").as(Date.class), queryLogRequest.getTo()));

            if (Objects.nonNull(queryLogRequest.getFrom())) {
                predicateList.add(criteriaBuilder.greaterThan(root.get("time").as(Date.class), queryLogRequest.getFrom()));
            }

            Predicate[] predicates = new Predicate[predicateList.size()];
            predicateList.toArray(predicates);
            return criteriaQuery.where(predicates).getRestriction();
        }, pageRequest);

        QueryResult<Log> queryResult = new QueryResult<>(logs.getTotalElements(), logs.getContent());
        return QueryResponseData.ok(queryResult);
    }

    @Override
    @Transactional
    public void saveLog(Log log) {
        logRepository.save(log);
    }

    @Override
    @Transactional
    public BasicResponseData deleteLog(Long logId) {
        logRepository.deleteById(logId);
        return BasicResponseData.ok();
    }
}
