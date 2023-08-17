package ru.shvets.worldbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import ru.shvets.worldbank.dto.DataDTO;
import ru.shvets.worldbank.service.GdpService;

import java.util.List;

@Controller
public class GdpController {
    private final GdpService gdpService;

    @Autowired
    public GdpController(GdpService gdpService) {
        this.gdpService = gdpService;
    }

    @QueryMapping
    public List<DataDTO> getGdp(@Argument List<String> countryCodeList,
                                @Argument String startDate, @Argument String endDate,
                                @Argument String startValue, @Argument String endValue,
                                @Argument List<String> sortList,
                                @Argument String page, @Argument String perPage) {
        return gdpService.find(
                countryCodeList, startDate, endDate, startValue, endValue, sortList, page, perPage);
    }
}
