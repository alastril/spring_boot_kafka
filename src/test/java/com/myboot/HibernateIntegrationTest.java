package com.myboot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myboot.entity.User;
import com.myboot.repository.UserRepository;
import com.myboot.request.RequestDate;
import com.myboot.request.SortDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
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

@ActiveProfiles(profiles = {"Hibernate", "test"})
public class HibernateIntegrationTest extends MainTestClass {

    private static final Logger LOGGER = LogManager.getLogger(HibernateIntegrationTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    RequestDate requestDate;

    @Autowired
    UserRepository userRepository;

    @BeforeAll
    public void init() throws Exception {
        requestDate.setDirection(SortDirection.DESC);
        requestDate.setFieldsSorted(Arrays.asList("dateCreation", "id"));
    }

    @Test
    void checkUsersCountFindingByDateCreation() throws Exception {
        requestDate.setDate(LocalDate.parse("2023-07-24", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault()));
        requestDate.setCountItemsPerPage(10);
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<User> users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {
        });

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
        List<User> users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assert.isTrue(users.size() == 1, "Unexpected list size = " + users.size());
        users.forEach(user ->
                Assert.isTrue(user.getDateCreation().equals(requestDate.getDate()), "Wrong object dateCreation :" + user));

        requestDate.setPage(1);
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assert.isTrue(users.size() == 1, "Unexpected list size = " + users.size());
        users.forEach(user ->
                Assert.isTrue(user.getDateCreation().equals(requestDate.getDate()), "Wrong object dateCreation :" + user));
    }

    @Test
    void checkUsersCountFindingByDateCreationBefore() throws Exception {
        requestDate.setDate(LocalDate.parse("2023-07-24", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault()));
        requestDate.setCountItemsPerPage(10);
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/before")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<User> users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assert.isTrue(users.size() == 3, "Unexpected list size = " + users.size());
        users.forEach(user ->
                Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDate()) < 0, "Wrong object dateCreation :" + user));
    }

    @Test
    void checkUsersCountFindingByDateCreationBeforePages() throws Exception {
        requestDate.setDate(LocalDate.parse("2023-07-24", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault()));
        requestDate.setCountItemsPerPage(2);
        requestDate.setPage(0);
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/before")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<User> users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assert.isTrue(users.size() == 2, "Unexpected list size = " + users.size());
        users.forEach(user ->
                Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDate()) < 0, "Wrong object dateCreation :" + user));

        requestDate.setPage(1);
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/before")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {
        });

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
        requestDate.setCountItemsPerPage(10);
        requestDate.setPage(0);
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/between")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<User> users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {
        });

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
        requestDate.setPage(0);
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/between")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<User> users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assert.isTrue(users.size() == 3, "Unexpected list size = " + users.size());
        users.forEach(user -> {
            Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDateTo()) <= 0, "Wrong object dateTo :" + user);
            Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDateFrom()) >= 0, "Wrong object dateFrom :" + user);
        });

        requestDate.setPage(1);
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users/between")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assert.isTrue(users.size() == 1, "Unexpected list size = " + users.size());
        users.forEach(user -> {
            Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDateTo()) <= 0, "Wrong object dateTo :" + user);
            Assert.isTrue(user.getDateCreation().compareTo(requestDate.getDateFrom()) >= 0, "Wrong object dateFrom :" + user);
        });
    }

    @Test
    void checkAddingUser() throws Exception {
        User user = User.builder().userName("testName").orderList(List.of()).
                dateCreation(LocalDate.parse("1999-01-30", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault())).build();
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/hyber/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        User userResult = objectMapper.readValue(resultActions.getResponse().getContentAsString(), User.class);
        user.setId(userResult.getId());

        Assert.isTrue(userResult.equals(user), "User wasn't added");
        requestDate.setDate(user.getDateCreation());
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/hyber/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDate))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        List<User> users = objectMapper.readValue(resultActions.getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assert.isTrue(users.size() == 1, "wrong count users was added");
        users.forEach(u ->
                Assert.isTrue(u.equals(user), "users not equal:" + u));
        userRepository.delete(user);//cleanup
    }

    @Test
    public void checkUserOptimisticLockTest() throws Exception {
        User user = User.builder().id(1L).userName("testName").orderList(List.of()).version(1).
                dateCreation(LocalDate.parse("1999-01-30", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .atTime(LocalDateTime.MIN.toLocalTime()).atZone(ZoneId.systemDefault())).build();
        MvcResult resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/hyber/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user))).andExpect(MockMvcResultMatchers.status().isBadRequest()).andReturn();
        Assert.isTrue("Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect)".
                equals(resultActions.getResponse().getContentAsString()), "Bad Exception handle");
    }
}
