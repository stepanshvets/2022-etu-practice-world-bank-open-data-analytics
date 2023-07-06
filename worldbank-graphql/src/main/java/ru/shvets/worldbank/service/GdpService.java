package ru.shvets.worldbank.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shvets.worldbank.dto.DataDTO;
import ru.shvets.worldbank.model.Data;
import ru.shvets.worldbank.model.Gdp;
import ru.shvets.worldbank.repository.GdpRepository;
import ru.shvets.worldbank.util.DataValidator;

@Service
@Transactional(readOnly = true)
public class GdpService extends DataService<Gdp> {
    @Autowired
    public GdpService(GdpRepository gdpRepository, DataServiceUtil dataServiceUtil, ModelMapper modelMapper, DataValidator<Object> dataValidator) {
        super(gdpRepository, dataServiceUtil, modelMapper, dataValidator);
        startValue = (double) 0;
        endValue = 1.0E38;
    }

    @Override
    protected Data convertToData(DataDTO dataDTO) {
        return modelMapper.map(dataDTO, Gdp.class);
    }

    @Override
    protected DataDTO convertToDataDTO(Gdp data) {
        DataDTO dataDTO = modelMapper.map(data, DataDTO.class);
        dataDTO.setIndicator("Gdp");
        return dataDTO;
    }
}
