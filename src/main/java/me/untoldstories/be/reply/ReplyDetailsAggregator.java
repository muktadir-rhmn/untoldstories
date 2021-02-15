package me.untoldstories.be.reply;

import me.untoldstories.be.comment.dtos.Comment;
import me.untoldstories.be.reply.dtos.Reply;
import me.untoldstories.be.reply.repos.ReplyRepository;
import me.untoldstories.be.user.UserInternalAPI;
import me.untoldstories.be.user.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReplyDetailsAggregator {
    private final ReplyRepository replyRepository;
    private final UserInternalAPI userInternalAPI;

    @Autowired
    public ReplyDetailsAggregator(
            ReplyRepository replyRepository,
            UserInternalAPI userInternalAPI
    ) {
        this.replyRepository = replyRepository;
        this.userInternalAPI = userInternalAPI;
    }

    public List<Reply> fetchCommentsOfStory(long storyID) {
        List<Reply> replies = replyRepository.fetchRepliesOfStory(storyID);
        populateRemainingFields(replies);
        return replies;
    }

    private void populateRemainingFields(List<Reply> replies) {
        StringBuilder sbUserIDList = new StringBuilder();
        for (Reply reply: replies) {
            sbUserIDList.append(reply.author.id).append(',');
        }
        String userIDList = sbUserIDList.substring(0, sbUserIDList.length() - 1);

        Map<Long, User> userMap = userInternalAPI.fetchUserNamesByIDs(userIDList);
        for (Reply reply: replies) {
            reply.author.userName = userMap.get(reply.author.id).userName;
        }
    }
}
