import com.fasterxml.jackson.databind.ObjectMapper;
import com.myboot.Application;
import com.myboot.entity.Message;
import com.myboot.entity.Order;
import com.myboot.kafka.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles(profiles={"Publisher","Consumer"})
public class KafkaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private KafkaConsumer consumer;

    @Captor
    ArgumentCaptor<Message> messageArgumentCaptor;

    @Captor
    ArgumentCaptor<List<org.springframework.messaging.Message<List<Message>>>> messageBatchArgumentCaptor;

    @Captor
    ArgumentCaptor<List<Message>> messageListArgumentCaptor;
    @Captor
    ArgumentCaptor<List<String>> stringArgumentCaptor;

    @Test
    void checkListenerZeroToOnePartions() throws Exception {
        Message message = new Message("mess", new Order(2,"order"));

        mockMvc.perform(MockMvcRequestBuilders.post("/kafka/send")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(message))).andExpect(MockMvcResultMatchers.status().isOk());
        Thread.sleep(5000);//waiting replay topic answer
        Mockito.verify(consumer).listen(messageArgumentCaptor.capture());
        Assert.isTrue(!messageArgumentCaptor.getValue().getMessage().equals(message.getMessage()), "Message field must be Not equal objects");
        Mockito.verify(consumer, Mockito.atLeast(1)).listenReplyRead(stringArgumentCaptor.capture());
        Assert.isTrue(objectMapper.readValue(stringArgumentCaptor.getValue().toString(), List.class).size()>=1, "Not equal objects");
    }

    @Test
    void checkAllListeners() throws Exception {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("mess", new Order(2,"order")));
        messages.add(new Message("mess_second", new Order(3,"order2")));

        mockMvc.perform(MockMvcRequestBuilders.post("/kafka/sendToBatch")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(messages))).andExpect(MockMvcResultMatchers.status().isOk());
        Thread.sleep(5000);//waiting replay topic answer
        Mockito.verify(consumer).batchListener(messageBatchArgumentCaptor.capture());
        Assert.isTrue(messageBatchArgumentCaptor.getValue().stream().findFirst().get().getPayload().size() == 2, "Message list must be 2");
        Mockito.verify(consumer, Mockito.atLeast(1)).listenReplyRead(stringArgumentCaptor.capture());
        Assert.isTrue(objectMapper.readValue(stringArgumentCaptor.getValue().toString(), List.class).size() >= 2, "Size must be 2");
    }
}
