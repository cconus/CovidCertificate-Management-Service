package ch.admin.bag.covidcertificate.api.valueset;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class TestSetJson {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Date")
    private String date;
    @JsonProperty("Version")
    private String version;
    private List<TestValueSet> entries;
}
