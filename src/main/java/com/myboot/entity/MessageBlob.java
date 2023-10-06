package com.myboot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MessageBlob implements Message {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    long id;
    @JsonProperty("attachments")
    @ElementCollection
    List<Blob> attachments;

    String body;

    LocalDateTime createTime;

    @ManyToOne(cascade = CascadeType.ALL)
    User user;
}
