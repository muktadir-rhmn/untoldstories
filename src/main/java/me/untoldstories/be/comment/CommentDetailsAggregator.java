package me.untoldstories.be.comment;

import me.untoldstories.be.comment.pojos.Comment;
import me.untoldstories.be.comment.repos.CommentReactionRepository;
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
    private final CommentReactionRepository commentReactionRepository;
    private final UserInternalAPI userInternalAPI;

    @Autowired
    public CommentDetailsAggregator(
            CommentRepository commentRepository,
            CommentReactionRepository commentReactionRepository,
            UserInternalAPI userInternalAPI
    ) {
        this.commentRepository = commentRepository;
        this.commentReactionRepository = commentReactionRepository;
        this.userInternalAPI = userInternalAPI;
    }

    public List<Comment> fetchCommentsOfStory(long storyID, long requestingUserID) {
        List<Comment> comments = commentRepository.fetchCommentsOfStory(storyID);
        if (comments.size() == 0) return comments;

        populateRemainingFields(comments, storyID, requestingUserID);
        return comments;
    }

    private void populateRemainingFields(List<Comment> comments, long storyID, long requestingUserID) {
        StringBuilder sbUserIDList = new StringBuilder();
        for (Comment comment : comments) {
            sbUserIDList.append(comment.author.id).append(',');
        }
        String userIDList = sbUserIDList.substring(0, sbUserIDList.length() - 1);

        Map<Long, User> userMap = userInternalAPI.fetchUsersByIDs(userIDList);
        Map<Long, int[]> commentReactionMap = commentReactionRepository.fetchReactions(storyID);
        Map<Long, Byte> requestingUserReactionMap = commentReactionRepository.fetchUserReactions(storyID, requestingUserID);

        for (Comment comment : comments) {
            comment.author.userName = userMap.get(comment.author.id).userName;
            comment.myReaction = requestingUserReactionMap.getOrDefault(comment.id, (byte) 0);

            int[] commentReaction = commentReactionMap.get(comment.id);
            if (commentReaction == null) continue;
            comment.nLikes = commentReaction[0];
            comment.nDislikes = commentReaction[1];
        }
    }
}
