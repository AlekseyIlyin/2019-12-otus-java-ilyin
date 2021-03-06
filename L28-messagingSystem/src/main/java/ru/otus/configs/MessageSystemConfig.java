package ru.otus.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.otus.Serializers;
import ru.otus.service.handlers.CreateUserRequestHandler;
import ru.otus.service.handlers.GetUsersRequestHandler;
import ru.otus.front.FrontendService;
import ru.otus.front.UsersResponseHandler;
import ru.otus.messagesystem.MessageSystem;
import ru.otus.messagesystem.MessageType;
import ru.otus.messagesystem.MsClient;
import ru.otus.messagesystem.MsClientImpl;
import ru.otus.repository.UserRepository;

@Configuration
@ComponentScan
public class MessageSystemConfig {
    private static final String FRONTEND_SERVICE_CLIENT_NAME = "frontendService";
    private static final String DATABASE_SERVICE_CLIENT_NAME = "databaseService";

    @Bean
    public MsClient createDbClient(UserRepository userRepository, MessageSystem messageSystem, Serializers serializer) {
        MsClient databaseMsClient = new MsClientImpl(serializer, DATABASE_SERVICE_CLIENT_NAME, messageSystem);

        databaseMsClient.addHandler(MessageType.USER_DATA, new GetUsersRequestHandler(userRepository, serializer));
        databaseMsClient.addHandler(MessageType.CREATE_USER, new CreateUserRequestHandler(userRepository, serializer));
        messageSystem.addClient(databaseMsClient);

        return databaseMsClient;
    }

    @Bean
    public String getDatabaseServiceClientName() {
        return DATABASE_SERVICE_CLIENT_NAME;
    }

    @Bean(name = "frontendMsClient")
    public MsClient createFrontClient(MessageSystem messageSystem, Serializers serializer
            , FrontendService frontendService) {
        MsClient frontendMsClient = new MsClientImpl(serializer, FRONTEND_SERVICE_CLIENT_NAME, messageSystem);

        UsersResponseHandler usersResponseHandler = new UsersResponseHandler(serializer, frontendService);
        frontendMsClient.addHandler(MessageType.USER_DATA, usersResponseHandler);
        frontendMsClient.addHandler(MessageType.CREATE_USER, usersResponseHandler);
        messageSystem.addClient(frontendMsClient);

        return frontendMsClient;
    }
}
