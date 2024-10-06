package ee.srini.clientmanager.controller;

import ee.srini.clientmanager.config.SecurityConfig;
import ee.srini.clientmanager.model.*;
import ee.srini.clientmanager.service.ClientManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
@Import(SecurityConfig.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientManagerService clientManagerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user1")
    public void testListClients() throws Exception {
        CountryDTO country1 = new CountryDTO(1L, "Estonia");
        ClientDTO client1 = new ClientDTO(1L, "Mari", "Mumm", "user1", "mari@eesti.ee", "A. H. Tammsaare tee 56", country1, new Date());
        ClientDTO client2 = new ClientDTO(2L, "Juku", "Tamm", "user1", "juku@eesti.ee", "Raekoja plats 9", country1, new Date());
        List<ClientDTO> clients = Arrays.asList(client1, client2);

        when(clientManagerService.getClientsByUser(anyString())).thenReturn(clients);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("clients"))
                .andExpect(model().attribute("clients", clients))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Mari")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Juku")));
    }

    @Test
    public void testListClients_Unauthenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "user1")
    public void testEditNonExistentClient() throws Exception {
        Long nonExistentClientId = 999L;

        when(clientManagerService.getClientByIdAndUser(nonExistentClientId, "user1")).thenReturn(null);

        mockMvc.perform(get("/client/{id}", nonExistentClientId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "user1")
    public void testEditClient_BelongsToAnotherUser() throws Exception {
        Long clientId = 1L;
        CountryDTO country1 = new CountryDTO(1L, "Estonia");
        ClientDTO client1 = new ClientDTO(2L, "Mari", "Mumm", "user1", "mari@eesti.ee", "A. H. Tammsaare tee 56", country1, new Date());

        when(clientManagerService.getClientByIdAndUser(clientId, "user1")).thenReturn(null);

        mockMvc.perform(get("/client/{id}", clientId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "user1")
    public void testSaveClient_InvalidData() throws Exception {
        mockMvc.perform(post("/client")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("firstName", "")
                        .param("lastName", "Doe")
                        .param("username", "user1")
                        .param("address", "123 Main St")
                        .param("country.id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("client"))
                .andExpect(model().attributeHasFieldErrors("client", "firstName"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("First name is required")));
    }


}
