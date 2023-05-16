package ru.shvets.worldbank.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.shvets.worldbank.dto.DataDTO;
import ru.shvets.worldbank.service.GdpService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gdp")
public class GdpController {
    private final GdpService gdpService;

    @Autowired
    public GdpController(GdpService gdpService) {
        this.gdpService = gdpService;
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
        return gdpService.find(countryCodeList, startDate, endDate, startValue, endValue, sortList, page, perPage);
    }

    @PostMapping("")
    public void save(@RequestBody DataDTO dataDTO) {
        gdpService.save(dataDTO);
    }

    @PutMapping("")
    public void putEdit(DataDTO dataDTO) {

    }

    @PatchMapping("")
    public void patchEdit(DataDTO dataDTO) {

    }

    @DeleteMapping("")
    public void delete(DataDTO dataDTO) {

    }
}
