package me.untoldstories.be.reply;

import me.untoldstories.be.reply.dtos.Reply;
import me.untoldstories.be.reply.repos.ReplyReactionRepository;
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
    private final ReplyReactionRepository replyReactionRepository;
    private final UserInternalAPI userInternalAPI;

    @Autowired
    public ReplyDetailsAggregator(
            ReplyRepository replyRepository,
            ReplyReactionRepository replyReactionRepository,
            UserInternalAPI userInternalAPI
    ) {
        this.replyRepository = replyRepository;
        this.replyReactionRepository = replyReactionRepository;
        this.userInternalAPI = userInternalAPI;
    }

    public List<Reply> fetchCommentsOfComment(long commentID, long requestingUserID) {
        List<Reply> replies = replyRepository.fetchRepliesOfComment(commentID);
        populateRemainingFields(replies, requestingUserID);
        return replies;
    }

    private void populateRemainingFields(List<Reply> replies, long requestingUserID) {
        if (replies.size() == 0) return;

        StringBuilder sbReplyIDList = new StringBuilder();
        StringBuilder sbUserIDList = new StringBuilder();
        for (Reply reply: replies) {
            sbReplyIDList.append(reply.id).append(',');
            sbUserIDList.append(reply.author.id).append(',');
        }
        String replyIDList = sbReplyIDList.substring(0, sbReplyIDList.length() - 1);
        String userIDList = sbUserIDList.substring(0, sbUserIDList.length() - 1);

        Map<Long, User> userMap = userInternalAPI.fetchUsersByIDs(userIDList);
        Map<Long, int[]> replyReactionMap = replyReactionRepository.fetchReactions(replyIDList);
        Map<Long, Byte> requestingUserReactionMap = replyReactionRepository.fetchUserReaction(replyIDList, requestingUserID);

        for (Reply reply: replies) {
            reply.author.userName = userMap.get(reply.author.id).userName;
            reply.myReaction= requestingUserReactionMap.getOrDefault(reply.id, (byte)0);

            int[] replyReaction = replyReactionMap.get(reply.id);
            if (replyReaction == null) continue;
            reply.nLikes = replyReaction[0];
            reply.nDislikes = replyReaction[1];
        }
    }
}
