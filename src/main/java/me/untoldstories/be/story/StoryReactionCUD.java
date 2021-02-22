package me.untoldstories.be.story;

import me.untoldstories.be.constants.Reaction;
import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.story.repos.StoryReactionRepository;
import me.untoldstories.be.user.auth.pojos.SignedInUser;
import me.untoldstories.be.utils.pojos.SingleMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static me.untoldstories.be.story.MetaData.STORY_SERVICE_API_BASE_PATH;

@RestController
@RequestMapping(STORY_SERVICE_API_BASE_PATH)
public class StoryReactionCUD {
    private final StoryReactionRepository storyReactionRepository;

    @Autowired
    public StoryReactionCUD(StoryReactionRepository storyReactionRepository) {
        this.storyReactionRepository = storyReactionRepository;
    }

    @PostMapping("/{storyID}/like")
    public SingleMessageResponse like(
            @RequestAttribute("user") SignedInUser signedInUser,
            @PathVariable long storyID
    ) {
        storyReactionRepository.addAndRemovePreviousOnes(signedInUser.getUserID(), storyID, Reaction.LIKE);

        return SingleMessageResponse.OK;
    }

    @DeleteMapping("/{storyID}/reactions")
    public SingleMessageResponse removeReactions(
            @RequestAttribute("user") SignedInUser signedInUser,
            @PathVariable long storyID
    ) {
        boolean existed = storyReactionRepository.removeIfExists(signedInUser.getUserID(), storyID);

        if (existed) return SingleMessageResponse.OK;
        else throw SingleErrorMessageException.DOES_NOT_EXIST;
    }
}
