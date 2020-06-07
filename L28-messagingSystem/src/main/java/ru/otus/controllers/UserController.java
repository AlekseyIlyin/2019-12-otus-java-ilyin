package ru.otus.controllers;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import ru.otus.domain.User;
import ru.otus.front.FrontendService;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final FrontendService frontendService;

    public UserController(SimpMessagingTemplate messagingTemplate, FrontendService frontendService) {
        this.messagingTemplate = messagingTemplate;
        this.frontendService = frontendService;
    }

    @MessageMapping("/add")
    public void getMessage(String user) {
        final var userFromJson = new Gson().fromJson(user, User.class);
        frontendService.createUser(s -> {
            if ( !s.isEmpty() )
                frontendService.getUsersData(users ->
                        messagingTemplate.convertAndSend("/topic/list", users));
        }, userFromJson);
    }

    @SubscribeMapping("/list")
    public void sendUsers() {
        frontendService.getUsersData(s ->
                messagingTemplate.convertAndSend("/topic/list", s));
    }
}
