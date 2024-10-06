package ee.srini.clientmanager.repository;

import ee.srini.clientmanager.model.Client;
import ee.srini.clientmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByUser(User user);
}
