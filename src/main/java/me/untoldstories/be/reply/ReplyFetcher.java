package me.untoldstories.be.reply;

import me.untoldstories.be.reply.pojos.Reply;
import me.untoldstories.be.user.auth.pojos.SignedInUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static me.untoldstories.be.reply.MetaData.REPLY_SERVICE_API_BASE_PATH;

class FetchRepliesOfCommentResponse {
    public List<Reply> replies;
}

@RestController
@RequestMapping(REPLY_SERVICE_API_BASE_PATH)
public class ReplyFetcher {
    private final ReplyDetailsAggregator replyDetailsAggregator;

    @Autowired
    public ReplyFetcher(ReplyDetailsAggregator replyDetailsAggregator) {
        this.replyDetailsAggregator = replyDetailsAggregator;
    }

    @GetMapping("")
    public FetchRepliesOfCommentResponse fetchRepliesOfComment(
            @RequestAttribute("user") SignedInUser signedInUser,
            @RequestParam long commentID
    ) {
        FetchRepliesOfCommentResponse response = new FetchRepliesOfCommentResponse();
        response.replies = replyDetailsAggregator.fetchCommentsOfComment(commentID, signedInUser.getUserID());
        return response;
    }
}
