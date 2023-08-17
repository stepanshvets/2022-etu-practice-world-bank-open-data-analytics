package ru.shvets.worldbank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.shvets.worldbank.dto.DataDTO;
import ru.shvets.worldbank.util.DataIllegalArgumentException;
import ru.shvets.worldbank.util.DataNullPointerException;
import ru.shvets.worldbank.util.DataValidator;
import ru.shvets.worldbank.util.QueryParameterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class DataServiceUtil {
    private final DataValidator<Object> dataValidator;

    @Autowired
    public DataServiceUtil(DataValidator<Object> dataValidator) {
        this.dataValidator = dataValidator;
    }

    public Pageable getPageable(Integer page, Integer perPage, Sort sort) {
        if (page == null)
            return null;
        if (sort == null)
            return PageRequest.of(page, perPage);
        return PageRequest.of(page, perPage, sort);
    }

    public Sort getSort(List<String> sortList) {
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

    public void checkDataDTO(DataDTO dataDTO) {
        Set<String> violations = dataValidator.validate(dataDTO);
        if (!violations.isEmpty()) {
            String message = String.join(", ", violations);
            String article = (violations.size() > 1)? " are" : " is";
            throw new DataNullPointerException(message.substring(0, 1).toUpperCase()
                    + message.substring(1) + article + " null");
        }
    }

    public void checkData(Object obj) {
        Set<String> violations = dataValidator.validate(obj);
        if (!violations.isEmpty())
            throw new DataIllegalArgumentException("Invalid " +  String.join(", ", violations));
    }

    public int extractYearParameter(String yearString) {
        try {
            return Integer.parseInt(yearString);
        } catch (NumberFormatException e) {
            throw new QueryParameterException("The parameter value is invalid");
        }
    }
}
