package com.myboot.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.myboot.converters.ZonedDateTimeDeserializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class RequestDate {
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime date;
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime dateFrom;
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime dateTo;

    private int countItemsPerPage;
    private int page;
    private SortDirection direction = SortDirection.ASC;
    private List<String> fieldsSorted = new ArrayList<>();
}
