package ru.shvets.worldbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public List<DataDTO> find(@RequestParam(name = "country_code", required = false) List<String> countryCodeList,
                              @RequestParam(name = "start_date", required = false) String startDate,
                              @RequestParam(name = "end_date", required = false) String endDate,
                              @RequestParam(name = "start_value", required = false) String startValue,
                              @RequestParam(name = "end_value", required = false) String endValue,
                              @RequestParam(name = "sort", required = false) List<String> sortList,
                              @RequestParam(name = "page", required = false) String page,
                              @RequestParam(name = "per_page", required = false) String perPage) {
        return gdpService.find(countryCodeList, startDate, endDate, startValue, endValue, sortList, page, perPage);
    }

    @PostMapping("")
    public ResponseEntity<DataDTO> save(@RequestBody DataDTO dataDTO) {
        return new ResponseEntity<>(gdpService.save(dataDTO), HttpStatus.CREATED);
    }

    @PutMapping("")
    public ResponseEntity<DataDTO> putEdit(@RequestBody DataDTO dataDTO,
                        @RequestParam(name = "year") String year,
                        @RequestParam(name = "country_code") String countryCode) {
        return new ResponseEntity<>(gdpService.putEdit(dataDTO, year, countryCode), HttpStatus.CREATED);
    }

    @PatchMapping("")
    public ResponseEntity<DataDTO> patchEdit(@RequestBody DataDTO dataDTO,
                          @RequestParam(name = "year") String year,
                          @RequestParam(name = "country_code") String countryCode) {
        return new ResponseEntity<>(gdpService.patchEdit(dataDTO, year, countryCode), HttpStatus.CREATED);
    }

    @DeleteMapping("")
    public ResponseEntity<?> delete(@RequestParam(name = "year") String year,
                       @RequestParam(name = "country_code") String countryCode) {
        gdpService.delete(year, countryCode);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
