package top.hzwhzw.iwarticleservice.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    private Integer id;
    private String documentId;
    private String title;
    private String body;
    private String text;
    private String rawBodyText;
    private long views;
    private long likesCount;
    private long commentsCount;
    private Integer isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String authorDocumentId;
    private Long authorId;
}
