package com.myboot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Orders")
public class Order {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    long id;
    @JsonProperty("number")
    String numberOrder;

    //TODO
    @OneToMany
    List<MessageSimple> noticeMessage;

    String sum;

    String typeMoney;

    @ManyToMany
    List<Goods> goodsList;

    LocalDateTime createTime;

    @ManyToOne
    User customer;
}
