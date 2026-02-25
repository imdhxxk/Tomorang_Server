package kr.hs.after.Tomorang.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class chatRoom {

    private Long id;
    private String roomId;      // UUID로 생성된 고유 방 ID
    private String user1;       // 첫 번째 사용자
    private String user2;       // 두 번째 사용자
    private LocalDateTime createdAt;
}

