package ru.shvets.worldbank.repository;

import org.springframework.stereotype.Repository;
import ru.shvets.worldbank.model.Inflation;

@Repository
public interface InflationRepository extends DataRepository<Inflation> {
}
