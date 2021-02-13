package me.untoldstories.be.story;

import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.story.repos.StoryRepository;
import me.untoldstories.be.user.UserDescriptor;
import me.untoldstories.be.utils.dtos.SingleIDResponse;
import me.untoldstories.be.utils.dtos.SingleMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static me.untoldstories.be.story.MetaData.STORY_SERVICE_API_ROOT_PATH;

class AddUpdateStoryRequest {
    @NotBlank(message = "Story must not be empty")
    public String story;
}

@RestController
@RequestMapping(STORY_SERVICE_API_ROOT_PATH)
public final class StoryCUD {
    private final StoryRepository storyRepository;
    private final SingleErrorMessageException doesNotExists = new SingleErrorMessageException("Story does not exists");

    @Autowired
    public StoryCUD(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @PostMapping("")
    public SingleIDResponse addStory(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @RequestBody @Valid AddUpdateStoryRequest request
    ) {
        Long storyID = storyRepository.add(userDescriptor.getUserID(), request.story);
        return new SingleIDResponse(storyID);
    }

    @PutMapping("/{storyID}")
    public SingleMessageResponse deleteStory(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable Long storyID,
            @RequestBody @Valid AddUpdateStoryRequest request
    ) {
        boolean exists = storyRepository.updateIfExists(userDescriptor.getUserID(), storyID, request.story);

        if (exists) return SingleMessageResponse.SUCCESS_RESPONSE;
        else throw doesNotExists;
    }

    @DeleteMapping("/{storyID}")
    public SingleMessageResponse deleteStory(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable Long storyID
    ) {
        boolean exists = storyRepository.deleteIfExists(userDescriptor.getUserID(), storyID);
        if (exists) return SingleMessageResponse.SUCCESS_RESPONSE;
        else throw doesNotExists;
    }
}
