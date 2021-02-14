package me.untoldstories.be.comment;

import me.untoldstories.be.comment.repos.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CommentInternalAPI {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentInternalAPI(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public int fetchNumOfCommentsOfStory(long storyID) {
        return commentRepository.fetchNumOfCommentsOfStory(storyID);
    }

    public Map<Long, Integer> fetchNumOfCommentsOfStories(String storyIDList) {
        return commentRepository.fetchNumOfCommentsOfStory(storyIDList);
    }
}
