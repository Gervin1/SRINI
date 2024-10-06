package ee.srini.clientmanager.repository;

import ee.srini.clientmanager.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
