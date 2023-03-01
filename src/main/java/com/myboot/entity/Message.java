package com.myboot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @JsonProperty("Mess")
    String message;
    @JsonProperty("Obj")
    Object obj;
}
