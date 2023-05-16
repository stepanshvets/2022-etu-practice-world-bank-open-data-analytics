package ru.shvets.worldbank.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.shvets.worldbank.model.Gdp;

import java.util.List;

public interface GdpRepository extends JpaRepository<Gdp, Integer> {
    List<Gdp> findByYearIsGreaterThanEqualAndYearIsLessThanEqual(
            Integer startDate, Integer endDate, Sort sort);
    List<Gdp> findByYearIsGreaterThanEqualAndYearIsLessThanEqual(
            Integer startDate, Integer endDate, Pageable pageable);
    List<Gdp> findByCountryCodeInAndYearIsGreaterThanEqualAndYearIsLessThanEqual(
            List<String> countryCodeList, Integer startDate, Integer endDate, Sort sort);
    List<Gdp> findByCountryCodeInAndYearIsGreaterThanEqualAndYearIsLessThanEqual(
            List<String> countryCodeList, Integer startDate, Integer endDate, Pageable pageable);

    @Query(value = "SELECT g FROM Gdp g WHERE g.year >= :startDate AND g.year <= :endDate" +
            " AND g.value >= :startValue AND g.value <= :endValue")
    List<Gdp> find(@Param("startDate")Integer startDate, @Param("endDate")Integer endDate,
                   @Param("startValue")Double startValue, @Param("endValue")Double endValue,
                   Sort sort);

    @Query(value = "SELECT g FROM Gdp g WHERE g.countryCode IN (:countryCodeList)" +
            " AND g.year >= :startDate AND g.year <= :endDate" +
            " AND g.value >= :startValue AND g.value <= :endValue")
    List<Gdp> find(@Param("countryCodeList")List<String> countryCodeList,
                   @Param("startDate")Integer startDate, @Param("endDate")Integer endDate,
                   @Param("startValue")Double startValue, @Param("endValue")Double endValue,
                   Sort sort);

    @Query(value = "SELECT g FROM Gdp g WHERE g.year >= :startDate AND g.year <= :endDate" +
            " AND g.value >= :startValue AND g.value <= :endValue")
    List<Gdp> find(@Param("startDate")Integer startDate, @Param("endDate")Integer endDate,
                   @Param("startValue")Double startValue, @Param("endValue")Double endValue,
                   Pageable pageable);

    @Query(value = "SELECT g FROM Gdp g WHERE g.countryCode IN (:countryCodeList)" +
            " AND g.year >= :startDate AND g.year <= :endDate" +
            " AND g.value >= :startValue AND g.value <= :endValue")
    List<Gdp> find(@Param("countryCodeList")List<String> countryCodeList,
                   @Param("startDate")Integer startDate, @Param("endDate")Integer endDate,
                   @Param("startValue")Double startValue, @Param("endValue")Double endValue,
                   Pageable pageable);
}
