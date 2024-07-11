package com.surgee.stage_2.tests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.surgee.stage_2.model.User;
import com.surgee.stage_2.model.Organization;
import com.surgee.stage_2.DAO.OrganizationRepository;
import com.surgee.stage_2.DAO.UserRepository;
import com.surgee.stage_2.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthSpec {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Organization org;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        organizationRepository.deleteAll();

        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setPhone("1234567890");
        userRepository.save(user);
    }

    @Test
    public void shouldRegisterUserSuccessfully() throws Exception {
        User newUser = new User();
        newUser.setFirstName("Jane");
        newUser.setLastName("Doe");
        newUser.setEmail("jane.doe@example.com");
        newUser.setPassword("password");
        newUser.setPhone("0987654321");

        String token = jwtService.generateToken(newUser);

        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("jane.doe@example.com"))
                .andExpect(jsonPath("$.user.firstName").value("Jane"))
                .andExpect(jsonPath("$.user.lastName").value("Doe"))
                .andExpect(jsonPath("$.accessToken").exists());

        Organization createdOrg = userRepository.findById(newUser.getUserId()).get().getOrganizations().get(0);
        assert createdOrg != null;
        assert createdOrg.getName().equals("Jane's Organization");
        assert jwtService.extractEmail(token).equals("jane.doe@example.com");
        assert jwtService.isTokenValid(token, newUser);
    }

    @Test
    public void shouldFailIfRequiredFieldsAreMissing() throws Exception {
        User newUser = new User();
        newUser.setLastName("Doe");
        newUser.setEmail("jane.doe@example.com");
        newUser.setPassword("password");
        newUser.setPhone("0987654321");

        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldFailIfDuplicateEmail() throws Exception {
        User newUser = new User();
        newUser.setFirstName("Jane");
        newUser.setLastName("Doe");
        newUser.setEmail("john.doe@example.com");
        newUser.setPassword("password");
        newUser.setPhone("0987654321");

        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldLoginUserSuccessfully() throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "john.doe@example.com");
        credentials.put("password", "password");

        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.user.email").value("john.doe@example.com"));
    }

    @Test
    public void shouldFailToLoginWithInvalidCredentials() throws Exception {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "john.doe@example.com");
        credentials.put("password", "wrongpassword");

        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized());
    }
}
