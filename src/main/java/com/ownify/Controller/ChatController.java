package com.ownify.Controller;

import com.ownify.Model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    @MessageMapping("/chat")
    @SendTo("/topic/public")
    public ChatMessage send(ChatMessage message, Principal principal) {
        // message.setSender(principal.getName());  // Oturum açmış kullanıcının adı, security implemente edildiğinde yorumu kaldırabilirsiniz
        message.setSender("Murat");
        return message;
    }
}
