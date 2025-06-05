package com.myboot.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.myboot.converters.ZonedDateTimeDeserializer;
import com.myboot.converters.ZonedDateTimeSerializer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@org.hibernate.annotations.Cache(region = "user_reg", usage = CacheConcurrencyStrategy.READ_WRITE)
@ToString
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    @Column(name = "user_name")
    String userName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    @org.hibernate.annotations.Cache(region = "user_reg.orderList",usage = CacheConcurrencyStrategy.READ_WRITE)
    @BatchSize(size = 10)
    @JsonManagedReference
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
