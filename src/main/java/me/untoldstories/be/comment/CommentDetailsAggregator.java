package me.untoldstories.be.comment;

import me.untoldstories.be.comment.dtos.Comment;
import me.untoldstories.be.comment.repos.CommentRepository;
import me.untoldstories.be.user.UserInternalAPI;
import me.untoldstories.be.user.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommentDetailsAggregator {
    private final CommentRepository commentRepository;
    private final UserInternalAPI userInternalAPI;

    @Autowired
    public CommentDetailsAggregator(
            CommentRepository commentRepository,
            UserInternalAPI userInternalAPI
    ) {
        this.commentRepository = commentRepository;
        this.userInternalAPI = userInternalAPI;
    }

    public List<Comment> fetchCommentsOfStory(long storyID) {
        List<Comment> comments = commentRepository.fetchCommentsOfStory(storyID);
        populateRemainingFields(comments);
        return comments;
    }

    private void populateRemainingFields(List<Comment> comments) {
        StringBuilder sbUserIDList = new StringBuilder();
        for (Comment comment : comments) {
            sbUserIDList.append(comment.author.id).append(',');
        }
        String userIDList = sbUserIDList.substring(0, sbUserIDList.length() - 1);

        Map<Long, User> userMap = userInternalAPI.fetchUserNamesByIDs(userIDList);
        for (Comment comment : comments) {
            comment.author.userName = userMap.get(comment.author.id).userName;
        }
    }
}
