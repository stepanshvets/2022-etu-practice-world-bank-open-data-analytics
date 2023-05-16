package ru.shvets.worldbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.shvets.worldbank.dto.DataDTO;
import ru.shvets.worldbank.service.ExportsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exports")
public class ExportsController {
    private final ExportsService exportsService;

    @Autowired
    public ExportsController(ExportsService exportsService) {
        this.exportsService = exportsService;
    }

    @GetMapping("")
    public List<DataDTO> find(@RequestParam(name = "country_code",required = false) List<String> countryCodeList,
                              @RequestParam(name = "start_date",required = false) String startDate,
                              @RequestParam(name = "end_date",required = false) String endDate,
                              @RequestParam(name = "start_value",required = false) String startValue,
                              @RequestParam(name = "end_value",required = false) String endValue,
                              @RequestParam(name = "sort",required = false) List<String> sortList,
                              @RequestParam(name = "page",required = false) String page,
                              @RequestParam(name = "per_page",required = false) String perPage) {
        return exportsService.find(countryCodeList, startDate, endDate, startValue, endValue, sortList, page, perPage);
    }
}