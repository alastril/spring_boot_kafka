package com.myboot.entity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.myboot.converters.ZonedDateTimeDeserializer;
import com.myboot.converters.ZonedDateTimeSerializer;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    @Column(name = "user_name")
    String userName;

    @OneToMany(cascade = CascadeType.ALL)
    List<Order> orderList;

    @Column(name = "date_creation")
    @JsonProperty("dateCreation")
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    ZonedDateTime dateCreation;

    @Version
    @Column(columnDefinition = "int default 0")
    long version;
}
