package ru.shvets.worldbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ru.shvets.worldbank.dto.DataDTO;
import ru.shvets.worldbank.model.Inflation;
import ru.shvets.worldbank.repository.InflationRepository;
import ru.shvets.worldbank.service.InflationService;

import java.util.List;

@Controller
public class InflationController {
    private final InflationService inflationService;

    @Autowired
    public InflationController(InflationService inflationService) {
        this.inflationService = inflationService;
    }

    @QueryMapping
    public List<DataDTO> getInflation(@Argument List<String> countryCodeList,
                                      @Argument String startDate, @Argument String endDate,
                                      @Argument String startValue, @Argument String endValue,
                                      @Argument List<String> sortList,
                                      @Argument String page, @Argument String perPage) {
        return inflationService.find(
                countryCodeList, startDate, endDate, startValue, endValue, sortList, page, perPage);
    }
}
