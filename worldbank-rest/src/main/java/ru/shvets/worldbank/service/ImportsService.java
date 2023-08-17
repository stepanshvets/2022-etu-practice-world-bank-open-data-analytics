package ru.shvets.worldbank.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shvets.worldbank.dto.DataDTO;
import ru.shvets.worldbank.model.Imports;
import ru.shvets.worldbank.repository.ImportsRepository;
import ru.shvets.worldbank.util.DataIllegalArgumentException;
import ru.shvets.worldbank.util.DataNotFoundException;
import ru.shvets.worldbank.util.DataNullPointerException;
import ru.shvets.worldbank.util.QueryParameterException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ImportsService {
    private final ImportsRepository importsRepository;
    private final DataServiceUtil dataServiceUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public ImportsService(ImportsRepository importsRepository, DataServiceUtil dataServiceUtil, ModelMapper modelMapper) {
        this.importsRepository = importsRepository;
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
                    return convertToListDataDTO(importsRepository.find(startDate, endDate, startValue, endValue, sort));
                return convertToListDataDTO(
                        importsRepository.find(countryCodeList, startDate, endDate, startValue, endValue, sort));
            }
            if (countryCodeList == null)
                return convertToListDataDTO(importsRepository.find(startDate, endDate, startValue, endValue, pageable));
            return convertToListDataDTO(
                    importsRepository.find(countryCodeList, startDate, endDate, startValue, endValue, pageable));
        }
        catch (NumberFormatException e) {
            throw new QueryParameterException("The parameter value is invalid");
        }
    }

    @Transactional
    public DataDTO save(DataDTO dataDTO) throws DataIllegalArgumentException, DataNullPointerException {
        dataServiceUtil.checkDataDTO(dataDTO);
        Imports imports = convertToImports(dataDTO);
        dataServiceUtil.checkData(imports);
        imports.setCountryCode(imports.getCountryCode().toUpperCase());
        if (importsRepository.findByYearAndCountryCode(imports.getYear(), imports.getCountryCode()).isPresent())
            throw new DataIllegalArgumentException("Data with year " + imports.getYear() + " and country code "
                    + imports.getCountryCode() + " already exist");
        importsRepository.save(imports);
        return convertToDataDTO(imports);
    }

    @Transactional
    public DataDTO putEdit(DataDTO dataDTO, String yearString, String countryCode)
            throws QueryParameterException, DataNullPointerException, DataIllegalArgumentException, DataNotFoundException {
        dataServiceUtil.checkDataDTO(dataDTO);
        Imports imports = convertToImports(dataDTO);
        int year = dataServiceUtil.extractYearParameter(yearString);

        Imports importsToUpdate = importsRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));
        imports.setId(importsToUpdate.getId());
        imports.setCountryCode(imports.getCountryCode().toUpperCase());
        importsRepository.save(imports);

        return convertToDataDTO(imports);
    }

    @Transactional
    public DataDTO patchEdit(DataDTO dataDTO, String yearString, String countryCode)
            throws QueryParameterException, DataIllegalArgumentException, DataNotFoundException {
        Imports imports = convertToImports(dataDTO);
        dataServiceUtil.checkData(imports);
        int year = dataServiceUtil.extractYearParameter(yearString);

        Imports importsToUpdate = importsRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));

        if (imports.getYear() != null)
            importsToUpdate.setYear(imports.getYear());
        if (imports.getCountryCode() != null)
            importsToUpdate.setCountryCode(imports.getCountryCode());
        if (imports.getCountry() != null)
            importsToUpdate.setCountry(imports.getCountry());
        if (imports.getValue() != null)
            importsToUpdate.setValue(imports.getValue());

        importsToUpdate.setCountryCode(importsToUpdate.getCountryCode().toUpperCase());
        return convertToDataDTO(importsToUpdate);
    }

    @Transactional
    public void delete(String yearString, String countryCode)
            throws QueryParameterException, DataNotFoundException {
        int year = dataServiceUtil.extractYearParameter(yearString);

        Imports importsToUpdate = importsRepository.findByYearAndCountryCode(year, countryCode).
                orElseThrow(() -> new DataNotFoundException("There is no data with these year and country code"));

        importsRepository.delete(importsToUpdate);
    }

    private Imports convertToImports(DataDTO dataDTO) {
        return modelMapper.map(dataDTO, Imports.class);
    }

    private DataDTO convertToDataDTO(Imports imports) {
        DataDTO dataDTO = modelMapper.map(imports, DataDTO.class);
        dataDTO.setIndicator("Imports");
        return dataDTO;
    }

    private List<DataDTO> convertToListDataDTO(List<Imports> list) {
        return list.stream().map(this::convertToDataDTO)
                .collect(Collectors.toList());
    }
}
