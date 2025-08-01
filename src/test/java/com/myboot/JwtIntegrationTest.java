package com.myboot;

import com.myboot.config.KafkaConfig;
import com.myboot.entity.MessageSimple;
import com.myboot.response.ErrorBody;
import com.myboot.security.JwtService;
import com.myboot.security.dto.JwtAuthenticationResponseDTO;
import com.myboot.security.dto.SignUpRequestDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

@ActiveProfiles(profiles = {"Hibernate","test"})
class JwtIntegrationTest extends KafkaConfig {

    private static final Logger LOGGER = LogManager.getLogger(JwtIntegrationTest.class);
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    JwtService jwtService;

    MessageSimple message = MessageSimple.builder().id(1L).body("test").build();

    @Test
    void hibernateAnonymousJwtTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/all")
                .contentType("application/json")).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void hibernateAuthRandomRoleJwtTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/auth/delete_user/2")
                .contentType("application/json")).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void authDeleteUserJwtTest() throws Exception {
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/auth/delete_user/2")
                .contentType("application/json")).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assert.isTrue("Deleted".equals(resultActions.getResponse().getContentAsString()), "Bad response!");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void authSignUpRoleJwtTest() throws Exception {
        Mockito.when(jwtService.generateToken(ArgumentMatchers.any(UserDetails.class))).thenReturn("gen_token");
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder().email("email").password("pass").username("user").build();
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/auth/sign_up")
                .contentType("application/json")
                        .content(getObjectMapper().writeValueAsString(signUpRequestDTO))).
                andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        JwtAuthenticationResponseDTO jwtAuthenticationResponseDTO = getObjectMapper().readValue(resultActions.getResponse().getContentAsString(), JwtAuthenticationResponseDTO.class);
        Assert.isTrue("gen_token".equals(jwtAuthenticationResponseDTO.getToken()), "Bad response!");
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    void authSignUpDuplicateRoleJwtTest() throws Exception {
        Mockito.when(jwtService.generateToken(ArgumentMatchers.any(UserDetails.class))).thenReturn("gen_token");
        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder().email("email_another").password("pass_another").username("user_another").build();
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/auth/sign_up")
                        .contentType("application/json")
                        .content(getObjectMapper().writeValueAsString(signUpRequestDTO))).
                andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        JwtAuthenticationResponseDTO jwtAuthenticationResponseDTO = getObjectMapper().readValue(resultActions.getResponse().getContentAsString(), JwtAuthenticationResponseDTO.class);
        Assert.isTrue("gen_token".equals(jwtAuthenticationResponseDTO.getToken()), "Bad response!");
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/auth/sign_up")
                        .contentType("application/json")
                        .content(getObjectMapper().writeValueAsString(signUpRequestDTO))).
                andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
      ErrorBody errorBody = getObjectMapper().readValue(resultActions.getResponse().getContentAsString(), ErrorBody.class);
      Assert.isTrue("User already exists!".equals(errorBody.getErrorMessage()), "Bad response!");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void authDeleteNotExistUserJwtTest() throws Exception {
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/auth/delete_user/9999")
                .contentType("application/json")).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        ErrorBody errorBody =
                getObjectMapper().readValue(resultActions.getResponse().getContentAsString(),
                        ErrorBody.class);
        Assert.isTrue("User not found!".equals(errorBody.getErrorMessage()), "Bad errorBody!" + errorBody);
    }

    @Test
    @WithMockUser(roles = "USER")
    void hibernateAuthUserRoleJwtTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/all")
                .contentType("application/json")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void hibernateAuthAdminRoleJwtTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/all")
                .contentType("application/json")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void kafkaAnonymousJwtTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/kafka/send")
                .contentType("application/json")).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void kafkaAuthUserRoleJwtTest() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.post("/kafka/send")
                .contentType("application/json")
                .content(getObjectMapper().writeValueAsString(message))).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void kafkaAuthAdminRoleJwtTest() throws Exception {
        getMockMvc().perform(MockMvcRequestBuilders.post("/kafka/send")
                .contentType("application/json")
                .content(getObjectMapper().writeValueAsString(message))).andExpect(MockMvcResultMatchers.status().isOk());
    }
}
