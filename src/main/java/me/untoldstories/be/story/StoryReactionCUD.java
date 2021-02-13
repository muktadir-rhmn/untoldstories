package me.untoldstories.be.story;

import me.untoldstories.be.constants.Reaction;
import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.story.repos.StoryReactionRepository;
import me.untoldstories.be.user.UserDescriptor;
import me.untoldstories.be.utils.dtos.SingleMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static me.untoldstories.be.story.MetaData.STORY_SERVICE_API_ROOT_PATH;

@RestController
@RequestMapping(STORY_SERVICE_API_ROOT_PATH)
public class StoryReactionCUD {
    private final StoryReactionRepository storyReactionRepository;

    @Autowired
    public StoryReactionCUD(StoryReactionRepository storyReactionRepository) {
        this.storyReactionRepository = storyReactionRepository;
    }

    @PostMapping("/{storyID}/like")
    public SingleMessageResponse like(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable long storyID
    ) {
        storyReactionRepository.addAndRemovePreviousOnes(userDescriptor.getUserID(), storyID, Reaction.LIKE);

        return SingleMessageResponse.OK;
    }

    @DeleteMapping("/{storyID}/reactions")
    public SingleMessageResponse removeReactions(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable long storyID
    ) {
        boolean existed = storyReactionRepository.removeIfExists(userDescriptor.getUserID(), storyID);

        if (existed) return SingleMessageResponse.OK;
        else throw SingleErrorMessageException.DOES_NOT_EXIST;
    }
}
