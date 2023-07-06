package ru.shvets.worldbank.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shvets.worldbank.dto.DataDTO;
import ru.shvets.worldbank.model.Data;
import ru.shvets.worldbank.repository.DataRepository;
import ru.shvets.worldbank.util.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public abstract class DataService<T extends Data> {
    protected final DataRepository<T> dataRepository;
    protected final DataServiceUtil dataServiceUtil;
    protected final ModelMapper modelMapper;
    protected Double startValue;
    protected Double endValue;

    @Autowired
    public DataService(DataRepository<T> dataRepository, DataServiceUtil dataServiceUtil, ModelMapper modelMapper, DataValidator<Object> dataValidator) {
        this.dataRepository = dataRepository;
        this.dataServiceUtil = dataServiceUtil;
        this.modelMapper = modelMapper;
    }

    public List<DataDTO> find(List<String> countryCodeList, String startDateString, String endDateString,
                              String startValueString, String endValueString, List<String> sortList,
                              String pageString, String perPageString)
            throws RuntimeException {
        try {
            Integer startDate = (startDateString == null) ? 0 : Integer.parseInt(startDateString);
            Integer endDate = (endDateString == null) ? LocalDate.now().getYear() : Integer.parseInt(endDateString);
            startValue = (startValueString == null) ? startValue : Double.parseDouble(startValueString);
            endValue = (endValueString == null) ? endValue : Double.parseDouble(endValueString);
            Sort sort = dataServiceUtil.getSort(sortList);
            Integer page = (pageString == null) ? null : Integer.parseInt(pageString);
            Integer perPage = (perPageString == null) ? 50 : Integer.parseInt(perPageString);
            Pageable pageable = dataServiceUtil.getPageable(page, perPage, sort);

            if (pageable == null) {
                if (countryCodeList == null)
                    return convertToListDataDTO(dataRepository.findByYearGreaterThanEqualAndYearLessThanEqualAndValueGreaterThanEqualAndValueLessThanEqual(
                            startDate, endDate, startValue, endValue, sort));
                return convertToListDataDTO(dataRepository.findByCountryCodeInAndYearGreaterThanEqualAndYearLessThanEqualAndValueGreaterThanEqualAndValueLessThanEqual(
                        countryCodeList, startDate, endDate, startValue, endValue, sort));
            }
            if (countryCodeList == null)
                return convertToListDataDTO(dataRepository.findByYearGreaterThanEqualAndYearLessThanEqualAndValueGreaterThanEqualAndValueLessThanEqual(
                        startDate, endDate, startValue, endValue, pageable));
            return convertToListDataDTO(dataRepository.findByCountryCodeInAndYearGreaterThanEqualAndYearLessThanEqualAndValueGreaterThanEqualAndValueLessThanEqual(
                    countryCodeList, startDate, endDate, startValue, endValue, pageable));
        }
        catch (NumberFormatException e) {
            throw new QueryParameterException("The parameter value is invalid");
        }
    }

    @Transactional
    public DataDTO save(DataDTO dataDTO) throws DataIllegalArgumentException, DataNullPointerException {
        dataServiceUtil.checkDataDTO(dataDTO);
        T data = (T) convertToData(dataDTO);
        dataServiceUtil.checkData(data);
        data.setCountryCode(data.getCountryCode().toUpperCase());
        if (dataRepository.findByYearAndCountryCode(data.getYear(), data.getCountryCode()).isPresent())
            throw new DataIllegalArgumentException("Data with year " + data.getYear() + " and country code "
                    + data.getCountryCode() + " already exist");
        dataRepository.save(data);
        return convertToDataDTO(data);
    }

    @Transactional
    public DataDTO putEdit(DataDTO dataDTO, String yearString, String countryCode)
            throws QueryParameterException, DataNullPointerException, DataIllegalArgumentException, DataNotFoundException {
        dataServiceUtil.checkDataDTO(dataDTO);
        T data = (T) convertToData(dataDTO);
        int year = dataServiceUtil.extractYearParameter(yearString);

        T dataToUpdate = dataRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));
        data.setId(dataToUpdate.getId());
        data.setCountryCode(data.getCountryCode().toUpperCase());
        dataRepository.save(data);

        return convertToDataDTO(data);
    }

    @Transactional
    public DataDTO patchEdit(DataDTO dataDTO, String yearString, String countryCode)
            throws QueryParameterException, DataIllegalArgumentException, DataNotFoundException {
        T data = (T) convertToData(dataDTO);
        dataServiceUtil.checkData(data);
        int year = dataServiceUtil.extractYearParameter(yearString);

        T dataToUpdate = dataRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));

        if (data.getYear() != null)
            dataToUpdate.setYear(data.getYear());
        if (data.getCountryCode() != null)
            dataToUpdate.setCountryCode(data.getCountryCode());
        if (data.getCountry() != null)
            dataToUpdate.setCountry(data.getCountry());
        if (data.getValue() != null)
            dataToUpdate.setValue(data.getValue());

        dataToUpdate.setCountryCode(dataToUpdate.getCountryCode().toUpperCase());
        return convertToDataDTO(dataToUpdate);
    }

    @Transactional
    public void delete(String yearString, String countryCode)
            throws QueryParameterException, DataNotFoundException {
        int year = dataServiceUtil.extractYearParameter(yearString);

        T dataToUpdate = dataRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));

        dataRepository.delete(dataToUpdate);
    }

    protected abstract Data convertToData(DataDTO dataDTO);

    protected abstract DataDTO convertToDataDTO(T data);

    private List<DataDTO> convertToListDataDTO(List<T> list) {
        return list.stream().map(this::convertToDataDTO)
                .collect(Collectors.toList());
    }
}
