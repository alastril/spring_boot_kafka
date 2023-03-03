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
    ArgumentCaptor<String> stringArgumentCaptor;

    @Test
    void checkAllListeners() throws Exception {
        Message message = new Message("mess", new Order(2,"order"));

        mockMvc.perform(MockMvcRequestBuilders.post("/kafka/send")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(message))).andExpect(MockMvcResultMatchers.status().isOk());
        Thread.sleep(5000);
        Mockito.verify( consumer).listen(messageArgumentCaptor.capture());
        Assert.isTrue(!messageArgumentCaptor.getValue().getMessage().equals(message.getMessage()), "Not equal objects");
        Mockito.verify( consumer).listenReplyRead(stringArgumentCaptor.capture());
        Assert.isTrue(objectMapper.readValue(stringArgumentCaptor.getValue(), Message.class).getMessage().equals("updated message"), "Not equal objects");
    }
}
