package org.dailycodebuffer.codebufferspringbootmongodb.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Profile("codebuffer")
@Builder
@Document(collection = "person")
@Schema(description = "Person Model Information")
@JsonInclude(JsonInclude.Include.NON_NULL)          //  It is commonly used in Spring Boot applications to exclude properties with empty/null/default values from serialization
public class Person  {

    // earlier it was String
    /**
     * @Field is related to how the data is stored in MongoDB, controlling the mapping between a Java field and the MongoDB document field.
     * @JsonProperty is related to JSON serialization and deserialization, controlling how a Java field maps to JSON keys.
     */
    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Person Id", example = "1234567")
    @JsonProperty(value="personId")
    @Field("_id")
    private ObjectId personId;

    @NotBlank
    @Schema(description = "Person FirstName", example = "Rahul")
    @Size(message = "Person FirstName",min = 3,max=23)
    private String firstName;

    @NotBlank
    @Schema(description = "Person LastName", example = "Sharma")
    @Size(message = "Person LastName",min = 3,max=23)
    private String lastName;

    @NotNull
    @Schema(description = "Person Age", example = "18")
    private Integer age;

    @NotEmpty
    @Schema(description = "Person Hobbies", example = "Playing Chess")
    private List<String> hobbies;

    @NotEmpty
    @Schema(description = "Person Address", example = "Karnataka , Koramangala, Bengaluru")
    private List<Address> addresses;
}
