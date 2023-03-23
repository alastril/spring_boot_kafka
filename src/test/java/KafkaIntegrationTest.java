import com.fasterxml.jackson.databind.ObjectMapper;
import com.myboot.Application;
import com.myboot.entity.MessageSimple;
import com.myboot.kafka.KafkaConsumer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles(profiles={"Publisher","Consumer"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KafkaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    KafkaAdmin kafkaAdmin;
    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private KafkaConsumer consumer;

    @Captor
    ArgumentCaptor<MessageSimple> messageArgumentCaptor;

    @Captor
    ArgumentCaptor<List<org.springframework.messaging.Message<List<MessageSimple>>>> messageBatchArgumentCaptor;

    @Captor
    ArgumentCaptor<List<MessageSimple>> messageListArgumentCaptor;
    @Captor
    ArgumentCaptor<List<String>> stringArgumentCaptor;

    @BeforeAll
    public void waitingKafkaInit() throws Exception {
        Thread.sleep(3000);//value depends on system
    }

    @Test
    void checkListenerZeroToOnePartitions() throws Exception {
        MessageSimple message = new MessageSimple(1L, "order");

        mockMvc.perform(MockMvcRequestBuilders.post("/kafka/send")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(message))).andExpect(MockMvcResultMatchers.status().isOk());
        Thread.sleep(500);//waiting replay topic answer
        Mockito.verify(consumer).listener(messageArgumentCaptor.capture());
        Assert.isTrue(!messageArgumentCaptor.getValue().getBody().equals(message.getBody()), "Message field must be Not equal objects");
        Mockito.verify(consumer, Mockito.atLeast(1)).listenReplyRead(stringArgumentCaptor.capture());
        Assert.isTrue(objectMapper.readValue(stringArgumentCaptor.getValue().toString(), List.class).size()>=1, "Not equal objects");
    }

    @Test
    void checkBatchListenerWithReply() throws Exception {
        List<MessageSimple> messages = new ArrayList<>();
        messages.add(new MessageSimple(1L, "order"));
        messages.add(new MessageSimple(2L, "order2"));

        mockMvc.perform(MockMvcRequestBuilders.post("/kafka/sendToBatch")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(messages))).andExpect(MockMvcResultMatchers.status().isOk());
        Thread.sleep(500);//waiting replay topic answer
        Mockito.verify(consumer).batchListener(messageBatchArgumentCaptor.capture());
        Assert.isTrue(messageBatchArgumentCaptor.getValue().stream().findFirst().get().getPayload().size() == 2, "Message list must be 2");
        Mockito.verify(consumer, Mockito.atLeast(1)).listenReplyRead(stringArgumentCaptor.capture());
        Assert.isTrue(objectMapper.readValue(stringArgumentCaptor.getValue().toString(), List.class).size() >= 2, "Size must be 2");
    }
}
