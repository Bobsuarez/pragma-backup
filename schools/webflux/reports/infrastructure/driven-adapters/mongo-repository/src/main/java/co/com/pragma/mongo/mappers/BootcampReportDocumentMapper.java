package co.com.pragma.mongo.mappers;

import co.com.pragma.model.BootcampReport;
import co.com.pragma.mongo.document.BootcampReportDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BootcampReportDocumentMapper {
    
    /**
     * Convierte modelo de dominio a documento MongoDB
     */
    @Mapping(target = "id", expression = "java(modelo.getId() != null ? modelo.getId().toString() : null)")
    BootcampReportDocument toDocument(BootcampReport modelo);
    
    /**
     * Convierte documento MongoDB a modelo de dominio
     */
    default BootcampReport toDomain(BootcampReportDocument document) {
        if (document == null) {
            return null;
        }
        return BootcampReport.builder()
                .bootcampId(document.getBootcampId())
                .bootcampName(document.getBootcampName())
                .bootcampDescription(document.getBootcampDescription())
                .durationMonths(document.getDurationMonths())
                .launchDate(document.getLaunchDate())
                .capabilitiesCount(document.getCapabilitiesCount())
                .technologiesCount(document.getTechnologiesCount())
                .enrolledPeopleCount(document.getEnrolledPeopleCount())
                .createdAt(document.getCreatedAt())
                .build();
    }
}
