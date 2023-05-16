package ru.shvets.worldbank.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.shvets.worldbank.dto.DataDTO;
import ru.shvets.worldbank.model.Gdp;
import ru.shvets.worldbank.repository.GdpRepository;
import ru.shvets.worldbank.util.QueryParameterException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GdpService {
    private final GdpRepository gdpRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public GdpService(GdpRepository gdpRepository, ModelMapper modelMapper) {
        this.gdpRepository = gdpRepository;
        this.modelMapper = modelMapper;
    }

    public List<DataDTO> find(List<String> countryCodeList, String startDateString, String endDateString,
                              String startValueString, String endValueString, List<String> sortList,
                              String pageString, String perPageString)
            throws RuntimeException {
        try {
            Integer startDate = (startDateString == null)? 0: Integer.parseInt(startDateString);
            Integer endDate = (endDateString == null)? LocalDate.now().getYear() : Integer.parseInt(endDateString);
            Double startValue = (startValueString == null)? 0: Double.parseDouble(startValueString);
            Double endValue = (endValueString == null)? 99999999999999999998.0: Double.parseDouble(endValueString);
            Sort sort = getSort(sortList);
            Integer page = (pageString == null)? null: Integer.parseInt(pageString);
            Integer perPage = (perPageString == null)? 50: Integer.parseInt(perPageString);
            Pageable pageable = getPageable(page, perPage, sort);

            if (pageable == null) {
                if (countryCodeList == null)
                    return convertToListDataDTO(gdpRepository.find(startDate, endDate, startValue, endValue, sort));
                return convertToListDataDTO(
                        gdpRepository.find(countryCodeList, startDate, endDate, startValue, endValue, sort));
            }
            if (countryCodeList == null)
                return convertToListDataDTO(gdpRepository.find(startDate, endDate, startValue, endValue, pageable));
            return convertToListDataDTO(
                    gdpRepository.find(countryCodeList, startDate, endDate, startValue, endValue, pageable));
        }
        catch (NumberFormatException e) {
            throw new QueryParameterException("The parameter value is invalid");
        }
    }

    private Pageable getPageable(Integer page, Integer perPage, Sort sort) {
        if (page == null)
            return null;
        return PageRequest.of(page, perPage, sort);
    }

    private Sort getSort(List<String> sortList) {
        if (sortList == null)
            return null;

        List<String> fieldNames = List.of("year", "country_code", "country", "value");
        List<Sort.Order> orders = new ArrayList<>();
        for (String s: sortList) {
            String field = s.substring(1).toLowerCase();
            if (!fieldNames.contains(field))
                throw new QueryParameterException("The parameter value is invalid");
            if (s.startsWith("+"))
                orders.add(new Sort.Order(Sort.Direction.ASC, field));
            else if (s.startsWith("-"))
                orders.add(new Sort.Order(Sort.Direction.DESC, field));
            else
                throw new QueryParameterException("The parameter value is invalid");
        }
        return Sort.by(orders);
    }

    public void save(DataDTO dataDTO) {

    }

    private DataDTO convertToDataDTO(Gdp gdp) {
        DataDTO dataDTO = modelMapper.map(gdp, DataDTO.class);
        dataDTO.setIndicator("GDP");
        return dataDTO;
    }

    private List<DataDTO> convertToListDataDTO(List<Gdp> list) {
        return list.stream().map(this::convertToDataDTO)
                .collect(Collectors.toList());
    }
}
