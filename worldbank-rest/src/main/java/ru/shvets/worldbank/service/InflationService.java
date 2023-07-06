package ru.shvets.worldbank.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shvets.worldbank.dto.DataDTO;
import ru.shvets.worldbank.model.Inflation;
import ru.shvets.worldbank.repository.InflationRepository;
import ru.shvets.worldbank.util.DataIllegalArgumentException;
import ru.shvets.worldbank.util.DataNotFoundException;
import ru.shvets.worldbank.util.DataNullPointerException;
import ru.shvets.worldbank.util.QueryParameterException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InflationService {
    private final InflationRepository inflationRepository;
    private final DataServiceUtil dataServiceUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public InflationService(InflationRepository inflationRepository, DataServiceUtil dataServiceUtil, ModelMapper modelMapper) {
        this.inflationRepository = inflationRepository;
        this.dataServiceUtil = dataServiceUtil;
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
            Double endValue = (endValueString == null)? 1.0E38: Double.parseDouble(endValueString);
            Sort sort = dataServiceUtil.getSort(sortList);
            Integer page = (pageString == null)? null: Integer.parseInt(pageString);
            Integer perPage = (perPageString == null)? 50: Integer.parseInt(perPageString);
            Pageable pageable = dataServiceUtil.getPageable(page, perPage, sort);

            if (pageable == null) {
                if (countryCodeList == null)
                    return convertToListDataDTO(inflationRepository.find(startDate, endDate, startValue, endValue, sort));
                return convertToListDataDTO(
                        inflationRepository.find(countryCodeList, startDate, endDate, startValue, endValue, sort));
            }
            if (countryCodeList == null)
                return convertToListDataDTO(inflationRepository.find(startDate, endDate, startValue, endValue, pageable));
            return convertToListDataDTO(
                    inflationRepository.find(countryCodeList, startDate, endDate, startValue, endValue, pageable));
        }
        catch (NumberFormatException e) {
            throw new QueryParameterException("The parameter value is invalid");
        }
    }

    @Transactional
    public DataDTO save(DataDTO dataDTO) throws DataIllegalArgumentException, DataNullPointerException {
        dataServiceUtil.checkDataDTO(dataDTO);
        Inflation inflation = convertToInflation(dataDTO);
        dataServiceUtil.checkData(inflation);
        inflation.setCountryCode(inflation.getCountryCode().toUpperCase());
        if (inflationRepository.findByYearAndCountryCode(inflation.getYear(), inflation.getCountryCode()).isPresent())
            throw new DataIllegalArgumentException("Data with year " + inflation.getYear() + " and country code "
                    + inflation.getCountryCode() + " already exist");
        inflationRepository.save(inflation);
        return convertToDataDTO(inflation);
    }

    @Transactional
    public DataDTO putEdit(DataDTO dataDTO, String yearString, String countryCode)
            throws QueryParameterException, DataNullPointerException, DataIllegalArgumentException, DataNotFoundException {
        dataServiceUtil.checkDataDTO(dataDTO);
        Inflation inflation = convertToInflation(dataDTO);
        int year = dataServiceUtil.extractYearParameter(yearString);

        Inflation inflationToUpdate = inflationRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));
        inflation.setId(inflationToUpdate.getId());
        inflation.setCountryCode(inflation.getCountryCode().toUpperCase());
        inflationRepository.save(inflation);

        return convertToDataDTO(inflation);
    }

    @Transactional
    public DataDTO patchEdit(DataDTO dataDTO, String yearString, String countryCode)
            throws QueryParameterException, DataIllegalArgumentException, DataNotFoundException {
        Inflation inflation = convertToInflation(dataDTO);
        dataServiceUtil.checkData(inflation);
        int year = dataServiceUtil.extractYearParameter(yearString);

        Inflation inflationToUpdate = inflationRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));

        if (inflation.getYear() != null)
            inflationToUpdate.setYear(inflation.getYear());
        if (inflation.getCountryCode() != null)
            inflationToUpdate.setCountryCode(inflation.getCountryCode());
        if (inflation.getCountry() != null)
            inflationToUpdate.setCountry(inflation.getCountry());
        if (inflation.getValue() != null)
            inflationToUpdate.setValue(inflation.getValue());

        inflationToUpdate.setCountryCode(inflationToUpdate.getCountryCode().toUpperCase());
        return convertToDataDTO(inflationToUpdate);
    }

    @Transactional
    public void delete(String yearString, String countryCode)
            throws QueryParameterException, DataNotFoundException {
        int year = dataServiceUtil.extractYearParameter(yearString);

        Inflation inflationToUpdate = inflationRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));

        inflationRepository.delete(inflationToUpdate);
    }

    private Inflation convertToInflation(DataDTO dataDTO) {
        return modelMapper.map(dataDTO, Inflation.class);
    }

    private DataDTO convertToDataDTO(Inflation inflation) {
        DataDTO dataDTO = modelMapper.map(inflation, DataDTO.class);
        dataDTO.setIndicator("Inflation");
        return dataDTO;
    }

    private List<DataDTO> convertToListDataDTO(List<Inflation> list) {
        return list.stream().map(this::convertToDataDTO)
                .collect(Collectors.toList());
    }
}
