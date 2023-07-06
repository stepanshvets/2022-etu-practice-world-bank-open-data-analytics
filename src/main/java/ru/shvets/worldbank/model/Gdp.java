package ru.shvets.worldbank.model;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Entity
@Table(name = "Gdp")
public class Gdp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Min(value = 1960, message = "year")
    @Max(value = 2023, message = "year")
    private Integer year;

    @Size(min = 2, max = 255, message = "country")
    private String country;

    @Size(min = 3, max = 3, message = "country code")
    private String countryCode;

    @Min(value = 0, message = "value")
    @Max(value = 999999999999999999L, message = "value")
    private Double value;

    public Gdp() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
