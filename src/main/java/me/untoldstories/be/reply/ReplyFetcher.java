package me.untoldstories.be.reply;

import me.untoldstories.be.reply.dtos.Reply;
import me.untoldstories.be.user.pojos.SignedInUserDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static me.untoldstories.be.reply.MetaData.REPLY_SERVICE_API_ROOT_PATH;

class FetchRepliesOfCommentResponse {
    public List<Reply> replies;
}

@RestController
@RequestMapping(REPLY_SERVICE_API_ROOT_PATH)
public class ReplyFetcher {
    private final ReplyDetailsAggregator replyDetailsAggregator;

    @Autowired
    public ReplyFetcher(ReplyDetailsAggregator replyDetailsAggregator) {
        this.replyDetailsAggregator = replyDetailsAggregator;
    }

    @GetMapping("")
    public FetchRepliesOfCommentResponse fetchRepliesOfComment(
            @RequestAttribute("user") SignedInUserDescriptor signedInUserDescriptor,
            @RequestParam long commentID
    ) {
        FetchRepliesOfCommentResponse response = new FetchRepliesOfCommentResponse();
        response.replies = replyDetailsAggregator.fetchCommentsOfComment(commentID, signedInUserDescriptor.getUserID());
        return response;
    }
}
