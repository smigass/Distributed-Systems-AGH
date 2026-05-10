package ds.agh.rabbitmq.rabbitmqsystem.admin;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Profile("admin")
@Component
public class MessagesStore {
    private final List<String> messages = new ArrayList<>();

    public void saveMessage(String message) {
        messages.add(message);
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }
}
