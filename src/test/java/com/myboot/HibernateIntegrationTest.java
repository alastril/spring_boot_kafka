package com.myboot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myboot.config.ConfigTestComposeFile;
import com.myboot.entity.User;
import com.myboot.request.RequestDate;
import com.myboot.request.SortDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles(profiles = {"Hibernate", "test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = ConfigTestComposeFile.class)
public class HibernateIntegrationTest {

    private static final Logger LOGGER = LogManager.getLogger(HibernateIntegrationTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    RequestDate requestDate;

    @BeforeAll
    public void init() throws Exception {
        requestDate.setDirection(SortDirection.DESC);
        requestDate.setFieldsSorted(Arrays.asList("dateCreation","id"));
    }

    @Test
    void checkUsersCountFindingByDateCreation() throws Exception {
        requestDate.setDate(LocalDate.parse("2023-07-24", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault()));
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<User> users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {});

        Assert.isTrue(users.size() == 2, "Unexpected list size!");
        users.forEach(user ->
                Assert.isTrue(user.getDateCreation().equals(requestDate.getDate()), "Wrong object dateCreation :" + user));
    }

    @Test
    void checkUsersCountFindingByDateCreationPages() throws Exception {
        requestDate.setDate(LocalDate.parse("2023-07-24", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault()));
        requestDate.setCountItemsPerPage(1);
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<User> users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {});

        Assert.isTrue(users.size() == 1, "Unexpected list size = " + users.size());
        users.forEach(user ->
                Assert.isTrue(user.getDateCreation().equals(requestDate.getDate()), "Wrong object dateCreation :" + user));

        requestDate.setPage(1);
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {});

        Assert.isTrue(users.size() == 1, "Unexpected list size = " + users.size());
        users.forEach(user ->
                Assert.isTrue(user.getDateCreation().equals(requestDate.getDate()), "Wrong object dateCreation :" + user));
    }

    @Test
    void checkUsersCountFindingByDateCreationBefore() throws Exception {
        requestDate.setDate(LocalDate.parse("2023-07-24", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault()));
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/before")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<User> users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {});

        Assert.isTrue(users.size() == 3, "Unexpected list size = " + users.size());
        users.forEach(user ->
                Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDate()) < 0, "Wrong object dateCreation :" + user));
    }

    @Test
    void checkUsersCountFindingByDateCreationBeforePages() throws Exception {
        requestDate.setDate(LocalDate.parse("2023-07-24", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault()));
        requestDate.setCountItemsPerPage(2);
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/before")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<User> users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {});

        Assert.isTrue(users.size() == 2, "Unexpected list size = " + users.size());
        users.forEach(user ->
                Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDate()) < 0, "Wrong object dateCreation :" + user));

        requestDate.setPage(1);
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/before")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {});

        Assert.isTrue(users.size() == 1, "Unexpected list size = " + users.size());
        users.forEach(user ->
                Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDate()) < 0, "Wrong object dateCreation :" + user));
    }

    @Test
    void checkUsersCountFindingByDateCreationBetween() throws Exception {
        requestDate.setDateFrom(LocalDate.parse("2023-07-23", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault()));
        requestDate.setDateTo(LocalDate.parse("2023-07-25", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault()));
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/between")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<User> users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {});

        Assert.isTrue(users.size() == 4, "Unexpected list size = " + users.size());
        users.forEach(user -> {
                Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDateTo()) <= 0, "Wrong object dateTo :" + user);
                Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDateFrom()) >= 0, "Wrong object dateFrom :" + user);
        });

    }

    @Test
    void checkUsersCountFindingByDateCreationBetweenPages() throws Exception {
        requestDate.setDateFrom(LocalDate.parse("2023-07-23", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault()));
        requestDate.setDateTo(LocalDate.parse("2023-07-25", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault()));
        requestDate.setCountItemsPerPage(3);
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/between")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<User> users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {});

        Assert.isTrue(users.size() == 3, "Unexpected list size = " + users.size());
        users.forEach(user -> {
            Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDateTo()) <= 0, "Wrong object dateTo :" + user);
            Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDateFrom()) >= 0, "Wrong object dateFrom :" + user);
        });

        requestDate.setPage(1);
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/between")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {});

        Assert.isTrue(users.size() == 1, "Unexpected list size = " + users.size());
        users.forEach(user -> {
            Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDateTo()) <= 0, "Wrong object dateTo :" + user);
            Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDateFrom()) >= 0, "Wrong object dateFrom :" + user);
        });
    }
}
