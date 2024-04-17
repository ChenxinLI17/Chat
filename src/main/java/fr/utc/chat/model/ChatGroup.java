package fr.utc.chat.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat_group")
public class ChatGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "chatTitle",unique = true)
    private String title;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "chatGroup", cascade = CascadeType.ALL)
    private Set<GroupMember> members = new HashSet<>();

    @OneToMany(mappedBy = "chatGroup", cascade = CascadeType.ALL)
    private Set<GroupMessage> messages = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "owner_mail")
    private String ownerMail;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<GroupMember> getMembers() {
        return members;
    }

    public void setMembers(Set<GroupMember> members) {
        this.members = members;
    }

    public Set<GroupMessage> getMessages() {
        return messages;
    }

    public void setMessages(Set<GroupMessage> messages) {
        this.messages = messages;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getOwnerMail() {
        if (this.owner != null) {
            return this.owner.getMail();
        } else {
            return null;
        }
    }

    public void setOwnerMail(String ownerMail) {
        this.ownerMail = ownerMail;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

}