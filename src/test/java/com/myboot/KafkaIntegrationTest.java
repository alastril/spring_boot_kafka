package com.myboot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.myboot.config.KafkaConfig;
import com.myboot.entity.MessageSimple;
import com.myboot.security.JwtAuthenticationFilter;
import com.myboot.security.dto.JwtAuthenticationResponseDTO;
import com.myboot.security.dto.SignInRequestDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


class KafkaIntegrationTest extends KafkaConfig {

    private static final Logger LOGGER = LogManager.getLogger(KafkaIntegrationTest.class);

    HttpHeaders headers = new HttpHeaders();

    @BeforeAll
    void waitingKafkaInit() throws Exception {
        awaitKafkaInit();
        SignInRequestDTO signInRequestDTO = SignInRequestDTO.builder().username("Tolya").password("pass").build();

        MvcResult resultActions = getMockMvc().perform(MockMvcRequestBuilders.post("/auth/sign_in")
                .contentType("application/json")
                .content(getObjectMapper().writeValueAsString(signInRequestDTO))).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        JwtAuthenticationResponseDTO jwtAuthenticationResponseDTO =
                getObjectMapper().readValue(resultActions.getResponse().getContentAsString(), JwtAuthenticationResponseDTO.class);
        headers.add(HttpHeaders.AUTHORIZATION,
                JwtAuthenticationFilter.BEARER_PREFIX + jwtAuthenticationResponseDTO.getToken());

        LOGGER.info("kafka init was finished...");
    }

    @Test
    void checkSendOneMessWithReply() throws Exception {
        MessageSimple message = new MessageSimple(1L, "order1");
        Awaitility.setDefaultTimeout(getAwaitSec(), TimeUnit.SECONDS);

        getMockMvc().perform(MockMvcRequestBuilders.post("/kafka/send")
                .contentType("application/json")
                .headers(headers)
                .content(getObjectMapper().writeValueAsString(message))).andExpect(MockMvcResultMatchers.status().isOk());

        Awaitility.await().atMost(Duration.ofSeconds(getAwaitSec())).untilAsserted(
                () -> {
                    Mockito.verify(getConsumer()).listener(getMessageArgumentCaptor().capture());
                    Assert.isTrue(getMessageArgumentCaptor().getValue().getPayload().getBody().equals(message.getBody()),
                            "Message field must be equal objects");
                    Mockito.verify(getConsumer()).listenReplyRead(getMessageArgumentCaptor().capture());
                    MessageSimple argMessageSimple = getMessageArgumentCaptor().getValue().getPayload();
                    Assert.isTrue(argMessageSimple.getId().equals(message.getId()) &&
                                    argMessageSimple.getBody().equals(message.getBody().toUpperCase()),
                            "Id should be equals!");
                });
    }

    @Test
    void checkBatchListenerWithReply() throws Exception {
        Awaitility.setDefaultTimeout(getAwaitSec(), TimeUnit.SECONDS);
        List<MessageSimple> messages = new ArrayList<>();
        messages.add(new MessageSimple(1L, "order1"));
        messages.add(new MessageSimple(2L, "order2"));

        getMockMvc().perform(MockMvcRequestBuilders.post("/kafka/sendToBatch")
                .contentType("application/json")
                .headers(headers)
                .content(getObjectMapper().writeValueAsString(messages))).andExpect(MockMvcResultMatchers.status().isOk());

        Awaitility.await().atMost(Duration.ofSeconds(getAwaitSec())).untilAsserted(
                () -> {
                    Mockito.verify(getConsumer()).batchListener(getMessageBatchArgumentCaptor().capture());
                    List<MessageSimple> messageResult = getMessageBatchArgumentCaptor().getValue().
                            stream().findFirst().get().getPayload();
                    Assert.isTrue(messageResult.size() == 2, "Message list must be 2");
                    messageResult.forEach(element ->
                            Assert.isTrue(messages.contains(element), "element not equal =" + element.toString())
                    );
                    Mockito.verify(getConsumer()).listenReplyListRead(getMessageBatchArgumentCaptor().capture());
                    messageResult = getObjectMapper().convertValue(getMessageBatchArgumentCaptor().getValue().stream().findFirst().get().getPayload(),
                            new TypeReference<>() {
                            });

                    Assert.isTrue(messageResult.size() == 2, "Message in reply list must be 2");
                    Assert.isTrue(messageResult.stream().filter(m ->
                            messages.stream().filter(orgMess ->
                                            m.getId().equals(orgMess.getId()) &&
                                                    m.getBody().equals(orgMess.getBody().toUpperCase()))
                                    .count() == 1).count() == 2, "Objects must be modified");
                });
    }
}
