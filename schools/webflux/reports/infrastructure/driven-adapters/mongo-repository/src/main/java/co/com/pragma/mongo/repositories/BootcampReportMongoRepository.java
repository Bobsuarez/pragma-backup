package co.com.pragma.mongo.repositories;

import co.com.pragma.mongo.document.BootcampReportDocument;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BootcampReportMongoRepository extends ReactiveMongoRepository<BootcampReportDocument, String> {
    
    @Query("{ 'bootcampId': ?0 }")
    Mono<BootcampReportDocument> findByBootcampId(Long bootcampId);
    
    @Query("{ 'bootcampId': ?0 }")
    Flux<BootcampReportDocument> findAllByBootcampId(Long bootcampId);
}
