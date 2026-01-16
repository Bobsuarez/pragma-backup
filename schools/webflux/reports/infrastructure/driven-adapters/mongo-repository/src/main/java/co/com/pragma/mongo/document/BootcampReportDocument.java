package co.com.pragma.mongo.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bootcamp_report")
public class BootcampReportDocument {
    @Id
    private String id;
    
    @Field("bootcampId")
    private Long bootcampId;
    
    @Field("bootcampName")
    private String bootcampName;

    @Field("bootcampDescription")
    private String bootcampDescription;

    @Field("durationMonths")
    private Integer durationMonths;
    
    @Field("capabilitiesCount")
    private Integer capabilitiesCount;

    @Field("launchDate")
    private String launchDate;
    
    @Field("technologiesCount")
    private Integer technologiesCount;
    
    @Field("enrolledPeopleCount")
    private Integer enrolledPeopleCount;
    
    @Field("createdAt")
    private LocalDateTime createdAt;
}
