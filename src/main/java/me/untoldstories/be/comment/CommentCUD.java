package me.untoldstories.be.comment;

import me.untoldstories.be.comment.repos.CommentRepository;
import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.user.UserDescriptor;
import me.untoldstories.be.utils.dtos.SingleIDResponse;
import me.untoldstories.be.utils.dtos.SingleMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static me.untoldstories.be.comment.MetaData.COMMENT_SERVICE_API_ROOT_PATH;


class AddCommentRequest {
    @NotBlank(message = "Comment must not be blank")
    public String comment;

    @Min(value = 0, message = "Invalid storyID")
    @NotNull(message = "Invalid storyID")
    public Long storyID;
}

class UpdateCommentRequest {
    @NotBlank(message = "Comment must not be blank")
    public String comment;
}

@RestController
@RequestMapping(COMMENT_SERVICE_API_ROOT_PATH)
public final class CommentCUD {
    private final CommentRepository commentRepository;
    private final SingleErrorMessageException doesNotExists = new SingleErrorMessageException("Comment does not exists");

    @Autowired
    public CommentCUD(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @PostMapping("")
    public SingleIDResponse addComment(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @RequestBody @Valid AddCommentRequest request
    ) {
        Long commentID = commentRepository.add(userDescriptor.getUserID(), request.comment, request.storyID);
        return new SingleIDResponse(commentID);
    }

    @PutMapping("/{commentID}")
    public SingleMessageResponse updateComment(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable long commentID,
            @RequestBody @Valid UpdateCommentRequest request
    ) {
        boolean exists = commentRepository.updateIfExists(userDescriptor.getUserID(), commentID, request.comment);

        if (exists) return SingleMessageResponse.SUCCESS_RESPONSE;
        else throw doesNotExists;
    }

    @DeleteMapping("/{commentID}")
    public SingleMessageResponse deleteComment(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable long commentID
    ) {
        boolean exists = commentRepository.deleteIfExists(userDescriptor.getUserID(), commentID);

        if (exists) return SingleMessageResponse.SUCCESS_RESPONSE;
        else throw doesNotExists;
    }
}
