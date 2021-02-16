package me.untoldstories.be.reply;


import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.reply.repos.ReplyRepository;
import me.untoldstories.be.user.pojos.UserDescriptor;
import me.untoldstories.be.utils.dtos.SingleIDResponse;
import me.untoldstories.be.utils.dtos.SingleMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static me.untoldstories.be.reply.MetaData.REPLY_SERVICE_API_ROOT_PATH;

class AddReplyRequest {
    @NotBlank(message = "Reply must not be blank")
    @Max(value = 1000, message = "Too lengthy")
    public String body;

    @NotNull(message = "commentID required")
    @Min(value = 0, message = "Invalid commentID")
    public Long commentID;

    @NotNull(message = "StoryID required")
    @Min(value = 0, message = "Invalid storyID")
    public Long storyID;
}

class UpdateReplyRequest {
    @NotBlank(message = "Reply must not be blank")
    @Max(value = 1000, message = "Too lengthy")
    public String body;
}

@RestController
@RequestMapping(REPLY_SERVICE_API_ROOT_PATH)
public final class ReplyCUD {
    private final ReplyRepository replyRepository;

    @Autowired
    public ReplyCUD(ReplyRepository replyRepository) {
        this.replyRepository = replyRepository;
    }

    @PostMapping("")
    public SingleIDResponse addReply(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @RequestBody @Valid AddReplyRequest request
    ) {
        Long replyID = replyRepository.add(userDescriptor.getUserID(), request.storyID, request.commentID, request.body);
        return new SingleIDResponse(replyID);
    }

    @PutMapping("/{replyID}")
    public SingleMessageResponse updateReply(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable long replyID,
            @RequestBody @Valid UpdateReplyRequest request
    ) {
        boolean exists = replyRepository.updateIfExists(userDescriptor.getUserID(), replyID, request.body);

        if (exists) return SingleMessageResponse.OK;
        else throw SingleErrorMessageException.DOES_NOT_EXIST;
    }

    @DeleteMapping("/{replyID}")
    public SingleMessageResponse deleteReply(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable long replyID
    ) {
        boolean exists = replyRepository.deleteIfExists(userDescriptor.getUserID(), replyID);

        if (exists) return SingleMessageResponse.OK;
        else throw SingleErrorMessageException.DOES_NOT_EXIST;
    }
}
