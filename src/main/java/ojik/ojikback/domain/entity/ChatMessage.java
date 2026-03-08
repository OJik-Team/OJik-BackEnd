package ojik.ojikback.domain.entity;

import java.time.LocalDateTime;

public class ChatMessage {
    private Long id;
    private TeamChatRoom chatRoom;
    private Member member;
    private String content;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public TeamChatRoom getChatRoom() {
        return chatRoom;
    }

    public Member getMember() {
        return member;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
