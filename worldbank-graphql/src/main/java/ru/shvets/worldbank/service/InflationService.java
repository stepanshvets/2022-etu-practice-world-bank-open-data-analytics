package ru.shvets.worldbank.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.shvets.worldbank.dto.DataDTO;
import ru.shvets.worldbank.model.Data;
import ru.shvets.worldbank.model.Inflation;
import ru.shvets.worldbank.repository.InflationRepository;
import ru.shvets.worldbank.util.DataValidator;

@Service
public class InflationService extends DataService<Inflation> {
    public InflationService(InflationRepository inflationRepository, DataServiceUtil dataServiceUtil, ModelMapper modelMapper, DataValidator<Object> dataValidator) {
        super(inflationRepository, dataServiceUtil, modelMapper, dataValidator);
        startValue = (double) -1000;
        endValue = 1.0E38;
    }

    @Override
    protected Data convertToData(DataDTO dataDTO) {
        return modelMapper.map(dataDTO, Inflation.class);
    }

    @Override
    protected DataDTO convertToDataDTO(Inflation data) {
        DataDTO dataDTO = modelMapper.map(data, DataDTO.class);
        dataDTO.setIndicator("Inflation");
        return dataDTO;
    }
}
