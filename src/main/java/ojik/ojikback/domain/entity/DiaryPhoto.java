package ojik.ojikback.domain.entity;

import java.time.LocalDateTime;

public class DiaryPhoto {
    private Long id;
    private Diary diary;
    private String photoUrl;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public Diary getDiary() {
        return diary;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
