package ru.shvets.worldbank.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shvets.worldbank.dto.DataDTO;
import ru.shvets.worldbank.model.Exports;
import ru.shvets.worldbank.repository.ExportsRepository;
import ru.shvets.worldbank.util.DataIllegalArgumentException;
import ru.shvets.worldbank.util.DataNotFoundException;
import ru.shvets.worldbank.util.DataNullPointerException;
import ru.shvets.worldbank.util.QueryParameterException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ExportsService {
    private final ExportsRepository exportsRepository;
    private final DataServiceUtil dataServiceUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public ExportsService(ExportsRepository exportsRepository, DataServiceUtil dataServiceUtil, ModelMapper modelMapper) {
        this.exportsRepository = exportsRepository;
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
                    return convertToListDataDTO(exportsRepository.find(startDate, endDate, startValue, endValue, sort));
                return convertToListDataDTO(
                        exportsRepository.find(countryCodeList, startDate, endDate, startValue, endValue, sort));
            }
            if (countryCodeList == null)
                return convertToListDataDTO(exportsRepository.find(startDate, endDate, startValue, endValue, pageable));
            return convertToListDataDTO(
                    exportsRepository.find(countryCodeList, startDate, endDate, startValue, endValue, pageable));
        }
        catch (NumberFormatException e) {
            throw new QueryParameterException("The parameter value is invalid");
        }
    }

    @Transactional
    public DataDTO save(DataDTO dataDTO) throws DataIllegalArgumentException, DataNullPointerException {
        dataServiceUtil.checkDataDTO(dataDTO);
        Exports exports = convertToExports(dataDTO);
        dataServiceUtil.checkData(exports);
        exports.setCountryCode(exports.getCountryCode().toUpperCase());
        if (exportsRepository.findByYearAndCountryCode(exports.getYear(), exports.getCountryCode()).isPresent())
            throw new DataIllegalArgumentException("Data with year " + exports.getYear() + " and country code "
                    + exports.getCountryCode() + " already exist");
        exportsRepository.save(exports);
        return convertToDataDTO(exports);
    }

    @Transactional
    public DataDTO putEdit(DataDTO dataDTO, String yearString, String countryCode)
            throws QueryParameterException, DataNullPointerException, DataIllegalArgumentException, DataNotFoundException {
        dataServiceUtil.checkDataDTO(dataDTO);
        Exports exports = convertToExports(dataDTO);
        int year = dataServiceUtil.extractYearParameter(yearString);

        Exports exportsToUpdate = exportsRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));
        exports.setId(exportsToUpdate.getId());
        exports.setCountryCode(exports.getCountryCode().toUpperCase());
        exportsRepository.save(exports);

        return convertToDataDTO(exports);
    }

    @Transactional
    public DataDTO patchEdit(DataDTO dataDTO, String yearString, String countryCode)
            throws QueryParameterException, DataIllegalArgumentException, DataNotFoundException {
        Exports exports = convertToExports(dataDTO);
        dataServiceUtil.checkData(exports);
        int year = dataServiceUtil.extractYearParameter(yearString);

        Exports exportsToUpdate = exportsRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));

        if (exports.getYear() != null)
            exportsToUpdate.setYear(exports.getYear());
        if (exports.getCountryCode() != null)
            exportsToUpdate.setCountryCode(exports.getCountryCode());
        if (exports.getCountry() != null)
            exportsToUpdate.setCountry(exports.getCountry());
        if (exports.getValue() != null)
            exportsToUpdate.setValue(exports.getValue());

        exportsToUpdate.setCountryCode(exportsToUpdate.getCountryCode().toUpperCase());
        return convertToDataDTO(exportsToUpdate);
    }

    @Transactional
    public void delete(String yearString, String countryCode)
            throws QueryParameterException, DataNotFoundException {
        int year = dataServiceUtil.extractYearParameter(yearString);

        Exports exportsToUpdate = exportsRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));

        exportsRepository.delete(exportsToUpdate);
    }

    private Exports convertToExports(DataDTO dataDTO) {
        return modelMapper.map(dataDTO, Exports.class);
    }

    private DataDTO convertToDataDTO(Exports exports) {
        DataDTO dataDTO = modelMapper.map(exports, DataDTO.class);
        dataDTO.setIndicator("Exports");
        return dataDTO;
    }

    private List<DataDTO> convertToListDataDTO(List<Exports> list) {
        return list.stream().map(this::convertToDataDTO)
                .collect(Collectors.toList());
    }
}
