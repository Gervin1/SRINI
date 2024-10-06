package ee.srini.clientmanager.service;

import ee.srini.clientmanager.model.*;
import ee.srini.clientmanager.repository.ClientRepository;
import ee.srini.clientmanager.repository.CountryRepository;
import ee.srini.clientmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientManagerService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;

    public List<ClientDTO> getClientsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Client> clients = clientRepository.findByUser(user);

        return clients.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ClientDTO getClientByIdAndUser(Long id, String username) {
        Client client = clientRepository.findById(id).orElse(null);
        if (client == null || !client.getUser().getUsername().equals(username)) {
            return null;
        }
        return convertToDTO(client);
    }

    public void saveClient(ClientDTO clientDTO, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Client client = convertToEntity(clientDTO);
        client.setUser(user);
        if (client.getCreatedAt() == null) {
            client.setCreatedAt(new Date());
        }

        clientRepository.save(client);
    }

    public List<CountryDTO> getAllCountries() {
        return countryRepository.findAll()
                .stream()
                .map(country -> new CountryDTO(country.getId(), country.getName()))
                .collect(Collectors.toList());
    }

    private ClientDTO convertToDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setUsername(client.getUsername());
        dto.setEmail(client.getEmail());
        dto.setAddress(client.getAddress());
        dto.setCreatedAt(client.getCreatedAt());
        if (client.getCountry() != null) {
            CountryDTO countryDTO = new CountryDTO();
            countryDTO.setId(client.getCountry().getId());
            countryDTO.setName(client.getCountry().getName());
            dto.setCountry(countryDTO);
        }
        return dto;
    }

    private Client convertToEntity(ClientDTO dto) {
        Client client = new Client();
        client.setId(dto.getId());
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setUsername(dto.getUsername());
        client.setEmail(dto.getEmail());
        client.setAddress(dto.getAddress());

        if (dto.getCountry() != null && dto.getCountry().getId() != null) {
            Country country = countryRepository.findById(dto.getCountry().getId())
                    .orElseThrow(() -> new RuntimeException("Country not found"));
            client.setCountry(country);
        }
        return client;
    }
}