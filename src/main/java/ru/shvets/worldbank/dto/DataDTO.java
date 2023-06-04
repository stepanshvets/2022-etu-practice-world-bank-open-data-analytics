package ru.shvets.worldbank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class DataDTO {
    private String indicator;

    @NotNull(message = "year")
    private Integer year;

    @NotNull(message = "country")
    private String country;

    @NotNull(message = "country code")
    @JsonProperty("country code")
    private String countryCode;

    @NotNull(message = "value")
    private Double value;

    public DataDTO() {
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
