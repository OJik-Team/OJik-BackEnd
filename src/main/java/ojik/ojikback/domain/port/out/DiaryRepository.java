package ojik.ojikback.domain.port.out;

import java.util.Optional;
import ojik.ojikback.domain.entity.Diary;

public interface DiaryRepository {
    Diary save(Diary diary);

    Optional<Diary> findById(Long diaryId);
}
