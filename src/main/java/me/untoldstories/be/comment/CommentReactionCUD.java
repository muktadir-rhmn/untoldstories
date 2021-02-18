package me.untoldstories.be.comment;

import me.untoldstories.be.comment.repos.CommentReactionRepository;
import me.untoldstories.be.constants.Reaction;
import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.user.pojos.SignedInUserDescriptor;
import me.untoldstories.be.utils.pojos.SingleMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static me.untoldstories.be.comment.MetaData.COMMENT_SERVICE_API_ROOT_PATH;

class AddCommentReactionRequest {
    @NotNull
    public Long storyID;
}

@RestController
@RequestMapping(COMMENT_SERVICE_API_ROOT_PATH)
public class CommentReactionCUD {
    private final CommentReactionRepository commentReactionRepository;

    @Autowired
    public CommentReactionCUD(CommentReactionRepository commentReactionRepository) {
        this.commentReactionRepository = commentReactionRepository;
    }

    @PostMapping("/{commentID}/like")
    public SingleMessageResponse like(
            @RequestAttribute("user") SignedInUserDescriptor signedInUserDescriptor,
            @PathVariable long commentID,
            @RequestBody @Valid AddCommentReactionRequest request
    ) {
        commentReactionRepository.addAndRemovePreviousOnes(signedInUserDescriptor.getUserID(), request.storyID, commentID, Reaction.LIKE);

        return SingleMessageResponse.OK;
    }

    @PostMapping("/{commentID}/dislike")
    public SingleMessageResponse dislike(
            @RequestAttribute("user") SignedInUserDescriptor signedInUserDescriptor,
            @PathVariable long commentID,
            @RequestBody @Valid AddCommentReactionRequest request
    ) {
        commentReactionRepository.addAndRemovePreviousOnes(signedInUserDescriptor.getUserID(), request.storyID, commentID, Reaction.DISLIKE);

        return SingleMessageResponse.OK;
    }

    @DeleteMapping("/{commentID}/reactions")
    public SingleMessageResponse removeReactions(
            @RequestAttribute("user") SignedInUserDescriptor signedInUserDescriptor,
            @PathVariable long commentID
    ) {
        boolean existed = commentReactionRepository.removeIfExists(signedInUserDescriptor.getUserID(), commentID);

        if (existed) return SingleMessageResponse.OK;
        else throw SingleErrorMessageException.DOES_NOT_EXIST;
    }
}
