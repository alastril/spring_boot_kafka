package com.myboot.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Orders")
@JsonInclude(JsonInclude.Include.NON_NULL)
@org.hibernate.annotations.Cache(region = "order",usage = CacheConcurrencyStrategy.READ_WRITE)
public class Order {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    long id;
    @JsonProperty("number")
    String numberOrder;

    @OneToMany(cascade = CascadeType.ALL)
    @org.hibernate.annotations.Cache(region = "order.noticeMessage",usage = CacheConcurrencyStrategy.READ_WRITE)
    List<MessageSimple> noticeMessage;

    String sum;

    String typeMoney;

    @ManyToMany(cascade = CascadeType.ALL)
    @org.hibernate.annotations.Cache(region = "order.goodsList",usage = CacheConcurrencyStrategy.READ_WRITE)
    List<Goods> goodsList;

    LocalDateTime createTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonBackReference
    User customer;
}
