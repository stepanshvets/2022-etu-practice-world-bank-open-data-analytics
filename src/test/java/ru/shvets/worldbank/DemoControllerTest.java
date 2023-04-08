package ru.shvets.worldbank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DemoControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(authorities = "read")
    public void testGetMethod() throws Exception {
        this.mockMvc.perform(get("/api/v1/developers")).andExpect(status().isOk());
    }

//    @Test
//    @WithMockUser(authorities = "write")
//    public void testPostMethod() throws Exception {
//        //todo
//        this.mockMvc.perform(post("/api/v1/developers")
//                        .content("\"id\":null,\"firstName\":\"TestFirstName\",\"lastName\":\"TestLastName\""))
//                .andExpect(status().isOk())
//                .andExpect(content().string(
//                        "\"id\":null,\"firstName\":\"TestFirstName\",\"lastName\":\"TestLastName\""));
//    }
}
