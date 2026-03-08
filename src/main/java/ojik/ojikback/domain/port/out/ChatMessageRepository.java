package ojik.ojikback.domain.port.out;

import java.util.Optional;
import ojik.ojikback.domain.entity.ChatMessage;

public interface ChatMessageRepository {
    ChatMessage save(ChatMessage chatMessage);

    Optional<ChatMessage> findById(Long chatMessageId);
}
