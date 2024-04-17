package fr.utc.chat.model;

import jakarta.persistence.*;

@Entity
@Table(name = "group_member")
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private ChatGroup chatGroup;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ChatGroup getChatGroup() {
        return chatGroup;
    }

    public void setChatGroup(ChatGroup chatGroup) {
        this.chatGroup = chatGroup;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GroupMember() {
    }

    public GroupMember(ChatGroup chatGroup, User user) {
        this.chatGroup = chatGroup;
        this.user = user;
    }

    public boolean isOwner() {
        if (this.getChatGroup() != null && this.getChatGroup().getOwner() != null && this.getUser() != null) {
            return this.getChatGroup().getOwner().equals(this.getUser());
        }
        return false;
    }

}

