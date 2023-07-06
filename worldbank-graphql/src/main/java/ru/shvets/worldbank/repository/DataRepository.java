package ru.shvets.worldbank.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.shvets.worldbank.model.Data;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public interface DataRepository<T extends Data> extends JpaRepository<T, Integer> {
    Optional<T> findByYearAndCountryCode(Integer year, String countryCode);

    List<T> findByYearGreaterThanEqualAndYearLessThanEqualAndValueGreaterThanEqualAndValueLessThanEqual(
            Integer startDate, Integer endDate,
            Double startValue, Double endValue, Sort sort);

    List<T> findByCountryCodeInAndYearGreaterThanEqualAndYearLessThanEqualAndValueGreaterThanEqualAndValueLessThanEqual(
            List<String> countryCodeList, Integer startDate, Integer endDate,
            Double startValue, Double endValue, Sort sort);

    List<T> findByYearGreaterThanEqualAndYearLessThanEqualAndValueGreaterThanEqualAndValueLessThanEqual(
            Integer startDate, Integer endDate,
            Double startValue, Double endValue, Pageable pageable);

    List<T> findByCountryCodeInAndYearGreaterThanEqualAndYearLessThanEqualAndValueGreaterThanEqualAndValueLessThanEqual(
            List<String> countryCodeList, Integer startDate, Integer endDate,
            Double startValue, Double endValue, Pageable pageable);
}
