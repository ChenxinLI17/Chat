package fr.utc.chat.component;

import fr.utc.chat.model.ChatGroup;
import fr.utc.chat.model.User;

import java.util.List;

public class UserChats {
    private User user;
    private List<ChatGroup> chats;

    public UserChats(User user, List<ChatGroup> chats) {
        this.user = user;
        this.chats = chats;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ChatGroup> getChats() {
        return chats;
    }

    public void setChats(List<ChatGroup> chats) {
        this.chats = chats;
    }
}
