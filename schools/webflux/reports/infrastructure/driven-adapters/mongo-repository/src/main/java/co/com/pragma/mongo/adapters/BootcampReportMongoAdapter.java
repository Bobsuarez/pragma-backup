package co.com.pragma.mongo.adapters;

import co.com.pragma.mongo.helper.AdapterOperations;
import co.com.pragma.mongo.document.BootcampReportDocument;
import co.com.pragma.mongo.mappers.BootcampReportDocumentMapper;
import co.com.pragma.mongo.repositories.BootcampReportMongoRepository;
import co.com.pragma.model.BootcampReport;
import co.com.pragma.model.gateway.BootcampReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class BootcampReportMongoAdapter extends AdapterOperations<BootcampReport, BootcampReportDocument, String, BootcampReportMongoRepository> implements BootcampReportRepository {

    public BootcampReportMongoAdapter(
            BootcampReportMongoRepository repository,
            ObjectMapper mapper,
            BootcampReportDocumentMapper documentMapper
    ) {
        super(repository, mapper, documentMapper::toDomain);
    }

    @Override
    public Mono<BootcampReport> save(BootcampReport bootcampReport) {
        log.debug("Saving bootcamp report for bootcampId: {}", bootcampReport.getBootcampId());
        return super.save(bootcampReport)
                .doOnSuccess(saved -> log.debug(
                        "Bootcamp report saved successfully for bootcampId: {}",
                        saved.getBootcampId()
                ))
                .doOnError(error -> log.error(
                        "Error saving bootcamp report for bootcampId: {}",
                        bootcampReport.getBootcampId(), error
                ));
    }
}
