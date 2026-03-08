package ojik.ojikback.domain.port.out;

import java.util.Optional;
import ojik.ojikback.domain.entity.TeamChatRoom;

public interface TeamChatRoomRepository {
    TeamChatRoom save(TeamChatRoom teamChatRoom);

    Optional<TeamChatRoom> findById(Long teamChatRoomId);
}
