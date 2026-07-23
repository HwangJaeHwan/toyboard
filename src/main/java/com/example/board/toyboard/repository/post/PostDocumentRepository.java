package com.example.board.toyboard.repository.post;

import com.example.board.toyboard.entity.post.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostDocumentRepository extends ElasticsearchRepository<PostDocument,String> {
}
