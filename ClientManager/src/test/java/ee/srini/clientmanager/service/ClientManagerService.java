package ee.srini.clientmanager.service;

import ee.srini.clientmanager.model.*;
import ee.srini.clientmanager.repository.ClientRepository;
import ee.srini.clientmanager.repository.CountryRepository;
import ee.srini.clientmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientManagerServiceTest {

    @InjectMocks
    private ClientManagerService clientManagerService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CountryRepository countryRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetClientsByUser() {
        String username = "user1";
        User user = new User(1L, username, "password", null);

        Country country = new Country(1L, "Estonia");
        Client client1 = new Client(1L, "John", "Doe", "user1", "john@example.com", "123 Main St", country, user,
                new Date());
        Client client2 = new Client(2L, "Jane", "Smith", "user1", "jane@example.com", "456 Elm St", country, user,
                new Date());
        List<Client> clients = Arrays.asList(client1, client2);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(clientRepository.findByUser(user)).thenReturn(clients);

        List<ClientDTO> clientDTOs = clientManagerService.getClientsByUser(username);

        assertNotNull(clientDTOs);
        assertEquals(2, clientDTOs.size());

        ClientDTO dto1 = clientDTOs.get(0);
        assertEquals(client1.getId(), dto1.getId());
        assertEquals(client1.getFirstName(), dto1.getFirstName());
        assertEquals(client1.getLastName(), dto1.getLastName());
        assertEquals(client1.getUsername(), dto1.getUsername());
        assertEquals(client1.getEmail(), dto1.getEmail());
        assertEquals(client1.getAddress(), dto1.getAddress());
        assertEquals(client1.getCountry().getId(), dto1.getCountry().getId());
        assertEquals(client1.getCountry().getName(), dto1.getCountry().getName());
        assertEquals(client1.getCreatedAt(), dto1.getCreatedAt());

        ClientDTO dto2 = clientDTOs.get(1);
        assertEquals(client2.getId(), dto2.getId());
        assertEquals(client2.getFirstName(), dto2.getFirstName());
        assertEquals(client2.getLastName(), dto2.getLastName());
        assertEquals(client2.getUsername(), dto2.getUsername());
        assertEquals(client2.getEmail(), dto2.getEmail());
        assertEquals(client2.getAddress(), dto2.getAddress());
        assertEquals(client2.getCountry().getId(), dto2.getCountry().getId());
        assertEquals(client2.getCountry().getName(), dto2.getCountry().getName());
        assertEquals(client2.getCreatedAt(), dto2.getCreatedAt());

        verify(userRepository, times(1)).findByUsername(username);
        verify(clientRepository, times(1)).findByUser(user);
    }

    @Test
    public void testGetClientsByUser_NonExistentUser() {
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            clientManagerService.getClientsByUser(username);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(clientRepository, never()).findByUser(any());
    }

    @Test
    public void testSaveClient_NonExistentUser() {
        String username = "nonExistentUser";
        ClientDTO clientDTO = new ClientDTO(null, "Juku", "Tamm", username, "juku@eesti.ee", "Raekoja Plats 9", new CountryDTO(1L, "Estonia"),
                null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            clientManagerService.saveClient(clientDTO, username);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(clientRepository, never()).save(any());
    }

    @Test
    public void testSaveClient_NonExistentCountry() {
        String username = "user1";
        User user = new User(1L, username, "password", null);

        ClientDTO clientDTO = new ClientDTO(null, "Charlie", "Chaplin", username, "charlie@example.com", "654 Maple St",
                new CountryDTO(999L, "NonExistentCountry"), null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientManagerService.saveClient(clientDTO, username);
        });

        assertEquals("Country not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(countryRepository, times(1)).findById(999L);
        verify(clientRepository, never()).save(any());
    }

    @Test
    public void testSaveClient_UpdateExistingClient() {
        String username = "user1";
        User user = new User(1L, username, "password", null);
        Long clientId = 1L;

        ClientDTO clientDTO = new ClientDTO(clientId, "Kalle", "Pall", username, "kalle@eesti.ee", "Koidu 2",
                new CountryDTO(1L, "Country1"), null);
        Country country = new Country(1L, "Country1");

        Client existingClient = new Client(clientId, "Malle", "Sall", username, "malle@eesti.ee", "Koidu 1",
                country, user, new Date());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

        clientManagerService.saveClient(clientDTO, username);

        verify(userRepository, times(1)).findByUsername(username);
        verify(countryRepository, times(1)).findById(1L);
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    public void testGetAllCountries_Normal() {
        Country country1 = new Country(1L, "Country1");
        Country country2 = new Country(2L, "Country2");
        List<Country> countries = Arrays.asList(country1, country2);

        when(countryRepository.findAll()).thenReturn(countries);

        List<CountryDTO> countryDTOs = clientManagerService.getAllCountries();

        assertNotNull(countryDTOs);
        assertEquals(2, countryDTOs.size());

        CountryDTO dto1 = countryDTOs.get(0);
        assertEquals(country1.getId(), dto1.getId());
        assertEquals(country1.getName(), dto1.getName());

        CountryDTO dto2 = countryDTOs.get(1);
        assertEquals(country2.getId(), dto2.getId());
        assertEquals(country2.getName(), dto2.getName());

        verify(countryRepository, times(1)).findAll();
    }
}
