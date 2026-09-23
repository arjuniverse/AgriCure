package com.agricure;

import com.agricure.dto.auth.LoginRequest;
import com.agricure.dto.auth.RegisterRequest;
import com.agricure.dto.identify.IdentifyRequest;
import com.agricure.dto.plant.PlantRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AgriCureApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void contextLoads() {
    }

    @Test
    void registerAndLoginReturnJwt() throws Exception {
        String email = "user-" + UUID.randomUUID() + "@farm.test";
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest("Pat Farmer", email, "Secret123"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("USER"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, "Secret123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void plantsCatalogueIsPublicAndSearchable() throws Exception {
        mockMvc.perform(get("/api/plants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));

        mockMvc.perform(get("/api/plants").param("search", "tomato"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tomato"));
    }

    @Test
    void identifyRequiresAuthThenReturnsMatches() throws Exception {
        mockMvc.perform(post("/api/identify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new IdentifyRequest(1L, List.of("leaf spots"), null))))
                .andExpect(status().isUnauthorized());

        String token = login("farmer@agricure.com", "Farmer@123");
        mockMvc.perform(post("/api/identify")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new IdentifyRequest(1L, List.of("concentric rings", "leaf spots", "yellowing"), "older leaves drying"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matches.length()").value(greaterThan(0)))
                .andExpect(jsonPath("$.plantName").value("Tomato"));
    }

    @Test
    void adminCanCreateAndDeletePlantWhileUserCannot() throws Exception {
        String userToken = login("farmer@agricure.com", "Farmer@123");
        PlantRequest body = new PlantRequest("Test Crop " + UUID.randomUUID(), "Testus cropus", "Vegetable",
                "Temporary plant used in API tests.", "Greenhouse", null);

        mockMvc.perform(post("/api/admin/plants")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());

        String adminToken = login("admin@agricure.com", "Admin@123");
        MvcResult created = mockMvc.perform(post("/api/admin/plants")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(delete("/api/admin/plants/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void diseaseDetailIncludesAffectedPlants() throws Exception {
        JsonNode diseases = objectMapper.readTree(mockMvc.perform(get("/api/diseases"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString());
        long id = diseases.get(0).get("id").asLong();
        mockMvc.perform(get("/api/diseases/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symptoms").isNotEmpty())
                .andExpect(jsonPath("$.treatment").isNotEmpty());
    }

    private String login(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, password))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
}
