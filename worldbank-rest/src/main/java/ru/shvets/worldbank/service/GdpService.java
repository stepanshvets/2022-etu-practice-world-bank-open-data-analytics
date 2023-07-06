package ru.shvets.worldbank.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shvets.worldbank.dto.DataDTO;
import ru.shvets.worldbank.model.Gdp;
import ru.shvets.worldbank.repository.GdpRepository;
import ru.shvets.worldbank.util.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GdpService {
    private final GdpRepository gdpRepository;
    private final DataServiceUtil dataServiceUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public GdpService(GdpRepository gdpRepository, DataServiceUtil dataServiceUtil, ModelMapper modelMapper, DataValidator<Object> dataValidator) {
        this.gdpRepository = gdpRepository;
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

    @Transactional
    public DataDTO save(DataDTO dataDTO) throws DataIllegalArgumentException, DataNullPointerException {
        dataServiceUtil.checkDataDTO(dataDTO);
        Gdp gdp = convertToGdp(dataDTO);
        dataServiceUtil.checkData(gdp);
        gdp.setCountryCode(gdp.getCountryCode().toUpperCase());
        if (gdpRepository.findByYearAndCountryCode(gdp.getYear(), gdp.getCountryCode()).isPresent())
            throw new DataIllegalArgumentException("Data with year " + gdp.getYear() + " and country code "
                    + gdp.getCountryCode() + " already exist");
        gdpRepository.save(gdp);
        return convertToDataDTO(gdp);
    }

    @Transactional
    public DataDTO putEdit(DataDTO dataDTO, String yearString, String countryCode)
            throws QueryParameterException, DataNullPointerException, DataIllegalArgumentException, DataNotFoundException {
        dataServiceUtil.checkDataDTO(dataDTO);
        Gdp gdp = convertToGdp(dataDTO);
        int year = dataServiceUtil.extractYearParameter(yearString);

        Gdp gdpToUpdate = gdpRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));
        gdp.setId(gdpToUpdate.getId());
        gdp.setCountryCode(gdp.getCountryCode().toUpperCase());
        gdpRepository.save(gdp);

        return convertToDataDTO(gdp);
    }

    @Transactional
    public DataDTO patchEdit(DataDTO dataDTO, String yearString, String countryCode)
            throws QueryParameterException, DataIllegalArgumentException, DataNotFoundException {
        Gdp gdp = convertToGdp(dataDTO);
        dataServiceUtil.checkData(gdp);
        int year = dataServiceUtil.extractYearParameter(yearString);

        Gdp gdpToUpdate = gdpRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));

        if (gdp.getYear() != null)
            gdpToUpdate.setYear(gdp.getYear());
        if (gdp.getCountryCode() != null)
            gdpToUpdate.setCountryCode(gdp.getCountryCode());
        if (gdp.getCountry() != null)
            gdpToUpdate.setCountry(gdp.getCountry());
        if (gdp.getValue() != null)
            gdpToUpdate.setValue(gdp.getValue());

        gdpToUpdate.setCountryCode(gdpToUpdate.getCountryCode().toUpperCase());
        return convertToDataDTO(gdpToUpdate);
    }

    @Transactional
    public void delete(String yearString, String countryCode)
            throws QueryParameterException, DataNotFoundException {
        int year = dataServiceUtil.extractYearParameter(yearString);

        Gdp gdpToUpdate = gdpRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));

        gdpRepository.delete(gdpToUpdate);
    }

    private Gdp convertToGdp(DataDTO dataDTO) {
        return modelMapper.map(dataDTO, Gdp.class);
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
