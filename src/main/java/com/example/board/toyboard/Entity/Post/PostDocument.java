package com.example.board.toyboard.Entity.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(indexName = "posts")
@Setting(settingPath = "/elasticsearch/post-settings.json")
@Getter
@AllArgsConstructor
@Builder
public class PostDocument {

    @Id
    private String id;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "standard"),
            otherFields = {
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer")
            }
    )
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String content;
    @Field(type = FieldType.Keyword)
    private String postType;
    @Field(type = FieldType.Long)
    private Long writerId;
    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "standard"),
            otherFields = {
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer")
            }
    )
    private String nickname;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdTime;


    public void highlightedTitle(String highlightedTitle) {
        this.title = highlightedTitle;
    }

    public void highlightedContent(String highlightedContent) {
        this.content = highlightedContent;
    }
}