package ojik.ojikback.domain.port.out;

import java.util.Optional;
import ojik.ojikback.domain.entity.DiaryPhoto;

public interface DiaryPhotoRepository {
    DiaryPhoto save(DiaryPhoto diaryPhoto);

    Optional<DiaryPhoto> findById(Long diaryPhotoId);
}
