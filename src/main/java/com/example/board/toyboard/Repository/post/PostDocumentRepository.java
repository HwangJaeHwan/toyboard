package com.example.board.toyboard.Repository.post;

import com.example.board.toyboard.Entity.Post.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostDocumentRepository extends ElasticsearchRepository<PostDocument,String> {
}
