package me.untoldstories.be.reply;


import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.reply.repos.ReplyRepository;
import me.untoldstories.be.user.UserDescriptor;
import me.untoldstories.be.utils.dtos.SingleIDResponse;
import me.untoldstories.be.utils.dtos.SingleMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static me.untoldstories.be.reply.MetaData.REPLY_SERVICE_API_ROOT_PATH;

class AddReplyRequest {
    @NotBlank(message = "Reply must not be blank")
    public String reply;

    @NotNull(message = "commentID required")
    @Min(value = 0, message = "Invalid commentID")
    public Long commentID;

    @NotNull(message = "StoryID required")
    @Min(value = 0, message = "Invalid storyID")
    public Long storyID;
}

class UpdateReplyRequest {
    @NotBlank(message = "Reply must not be blank")
    public String reply;
}

@RestController
@RequestMapping(REPLY_SERVICE_API_ROOT_PATH)
public final class ReplyCUD {
    private final ReplyRepository replyRepository;
    private final SingleErrorMessageException doesNotExists = new SingleErrorMessageException("Reply does not exists");

    @Autowired
    public ReplyCUD(ReplyRepository replyRepository) {
        this.replyRepository = replyRepository;
    }

    @PostMapping("")
    public SingleIDResponse addReply(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @RequestBody @Valid AddReplyRequest request
    ) {
        Long replyID = replyRepository.add(userDescriptor.getUserID(), request.storyID, request.commentID, request.reply);
        return new SingleIDResponse(replyID);
    }

    @PutMapping("/{replyID}")
    public SingleMessageResponse updateReply(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable long replyID,
            @RequestBody @Valid UpdateReplyRequest request
    ) {
        boolean exists = replyRepository.updateIfExists(userDescriptor.getUserID(), replyID, request.reply);

        if (exists) return SingleMessageResponse.SUCCESS_RESPONSE;
        else throw doesNotExists;
    }

    @DeleteMapping("/{replyID}")
    public SingleMessageResponse deleteReply(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable long replyID
    ) {
        boolean exists = replyRepository.deleteIfExists(userDescriptor.getUserID(), replyID);

        if (exists) return SingleMessageResponse.SUCCESS_RESPONSE;
        else throw doesNotExists;
    }
}
