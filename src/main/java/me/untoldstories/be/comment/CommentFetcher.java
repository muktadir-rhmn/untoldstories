package me.untoldstories.be.comment;

import me.untoldstories.be.comment.dtos.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static me.untoldstories.be.comment.MetaData.COMMENT_SERVICE_API_ROOT_PATH;

class FetchCommentsOfStoryResponse {
    public List<Comment> comments;
}

@RestController
@RequestMapping(COMMENT_SERVICE_API_ROOT_PATH)
public class CommentFetcher {
    private final CommentDetailsAggregator commentDetailsAggregator;

    @Autowired
    public CommentFetcher(
            CommentDetailsAggregator commentDetailsAggregator
    ) {
        this.commentDetailsAggregator = commentDetailsAggregator;
    }

    @GetMapping("")
    public FetchCommentsOfStoryResponse fetchCommentsOfStory (
            @RequestParam long storyID
    ) {
        FetchCommentsOfStoryResponse response = new FetchCommentsOfStoryResponse();
        response.comments = commentDetailsAggregator.fetchCommentsOfStory(storyID);
        return response;
    }
}
