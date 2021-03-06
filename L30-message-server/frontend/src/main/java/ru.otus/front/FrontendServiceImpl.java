package ru.otus.front;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.otus.domain.User;
import ru.otus.messagesystem.Message;
import ru.otus.messagesystem.MessageType;
import ru.otus.messagesystem.MsClient;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.springframework.messaging.simp.stomp.DefaultStompSession.EMPTY_PAYLOAD;

@Service
@Slf4j
public class FrontendServiceImpl implements FrontendService {

    private final Map<UUID, Consumer<?>> consumerMap = new ConcurrentHashMap<>();
    private final MsClient frontendMsClient;
    private final String databaseServiceClientName;

    public FrontendServiceImpl(MsClient frontendMsClient, String databaseServiceClientName) {
        this.frontendMsClient = frontendMsClient;
        this.databaseServiceClientName = databaseServiceClientName;
    }

    @Override
    public void getUsersData(Consumer<String> dataConsumer) {
        Message outMsg = frontendMsClient.produceMessage(databaseServiceClientName, EMPTY_PAYLOAD, MessageType.USER_DATA);
        consumerMap.put(outMsg.getId(), dataConsumer);
        frontendMsClient.sendMessage(outMsg);
    }

    @Override
    public void createUser(Consumer<String> dataConsumer, User user) {
        Message outMsg = frontendMsClient.produceMessage(databaseServiceClientName, user, MessageType.USER_CREATE);
        consumerMap.put(outMsg.getId(), dataConsumer);
        frontendMsClient.sendMessage(outMsg);
    }

    @Override
    public <T> Optional<Consumer<T>> takeConsumer(UUID sourceMessageId, Class<T> tClass) {
        Consumer<T> consumer = (Consumer<T>) consumerMap.remove(sourceMessageId);
        if (consumer == null) {
            log.warn ("consumer not found for:{}", sourceMessageId);
            return Optional.empty();
        }
        return Optional.of(consumer);
    }
}
