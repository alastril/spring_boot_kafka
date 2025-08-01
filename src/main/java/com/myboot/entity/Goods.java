package com.myboot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Goods {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    long id;
    String name;
    String description;
    @OneToMany(cascade = CascadeType.ALL)
    List<MessageBlob> feedbackMessages;

    @ElementCollection
    @MapKeyColumn(name="map_key")
    @Column(name="map_value")
    @CollectionTable(name="PROPERTIES_GOODS_MAPPING")
    Map<String, String> propertiesGoods;

    List<Blob> listPictures;

    String status;
}
