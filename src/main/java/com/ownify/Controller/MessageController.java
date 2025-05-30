package com.ownify.Controller;

import com.ownify.Entity.User;
import com.ownify.Entity.Message;
import com.ownify.Service.MessageService;
import com.ownify.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/messages")
    public String messages(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Message> messages = messageService.getUserMessages(user.getId());
        List<Message> unreadMessages = messageService.getUnreadMessages(user);
        
        model.addAttribute("messages", messages);
        model.addAttribute("unreadMessages", unreadMessages);
        model.addAttribute("user", user);
        
        return "messages";
    }
    
    @GetMapping("/messages/conversation/{userId}")
    public String conversation(@PathVariable Long userId, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        User otherUser = userService.findById(userId).orElse(null);
        if (otherUser == null) {
            return "redirect:/messages";
        }
        
        List<Message> conversation = messageService.getConversation(currentUser.getId(), userId);
        
        model.addAttribute("conversation", conversation);
        model.addAttribute("otherUser", otherUser);
        model.addAttribute("currentUser", currentUser);
        
        return "conversation";
    }
    
    @PostMapping("/messages/send")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestParam Long receiverId,
                                                         @RequestParam String content,
                                                         HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User sender = (User) session.getAttribute("user");
        if (sender == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return ResponseEntity.ok(response);
        }
        
        User receiver = userService.findById(receiverId).orElse(null);
        if (receiver == null) {
            response.put("success", false);
            response.put("message", "Receiver not found");
            return ResponseEntity.ok(response);
        }
        
        try {
            Message message = messageService.sendMessage(sender, receiver, content);
            response.put("success", true);
            response.put("message", "Message sent successfully");
            response.put("messageId", message.getId());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error sending message");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/messages/mark-read/{messageId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long messageId,
                                                        HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.put("success", false);
            response.put("message", "Please login first");
            return ResponseEntity.ok(response);
        }
        
        try {
            Message message = messageService.markAsRead(messageId);
            if (message != null) {
                response.put("success", true);
                response.put("message", "Message marked as read");
            } else {
                response.put("success", false);
                response.put("message", "Message not found");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error marking message as read");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/messages/unread-count")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> getUnreadCount(HttpSession session) {
        Map<String, Integer> response = new HashMap<>();
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.put("count", 0);
            return ResponseEntity.ok(response);
        }
        
        List<Message> unreadMessages = messageService.getUnreadMessages(user);
        response.put("count", unreadMessages.size());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/contact/{userId}")
    public String contactUser(@PathVariable Long userId, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        User contactUser = userService.findById(userId).orElse(null);
        if (contactUser == null) {
            return "redirect:/";
        }
        
        model.addAttribute("contactUser", contactUser);
        model.addAttribute("currentUser", currentUser);
        
        return "contact-user";
    }
}