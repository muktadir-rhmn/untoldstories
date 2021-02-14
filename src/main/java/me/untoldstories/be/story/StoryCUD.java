package me.untoldstories.be.story;

import me.untoldstories.be.constants.StoryPrivacy;
import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.story.repos.StoryRepository;
import me.untoldstories.be.user.pojos.UserDescriptor;
import me.untoldstories.be.utils.dtos.SingleIDResponse;
import me.untoldstories.be.utils.dtos.SingleMessageResponse;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static me.untoldstories.be.story.MetaData.STORY_SERVICE_API_ROOT_PATH;

class AddUpdateStoryRequest {
    @NotBlank(message = "Story must not be empty")
    public String story;

    @Range(min = StoryPrivacy.LOWEST_VALUE, max = StoryPrivacy.HIGHEST_VALUE)
    public int privacy;
}

class UpdateStoryPrivacyRequest {
    @Range(min = 1, max = 2)
    public int privacy;
}

@RestController
@RequestMapping(STORY_SERVICE_API_ROOT_PATH)
public final class StoryCUD {
    private final StoryRepository storyRepository;

    @Autowired
    public StoryCUD(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @PostMapping("")
    public SingleIDResponse addStory(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @RequestBody @Valid AddUpdateStoryRequest request
    ) {
        Long storyID = storyRepository.add(userDescriptor.getUserID(), request.story, request.privacy);
        return new SingleIDResponse(storyID);
    }

    @PutMapping("/{storyID}")
    public SingleMessageResponse updateStory(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable Long storyID,
            @RequestBody @Valid AddUpdateStoryRequest request
    ) {
        boolean exists = storyRepository.updateIfExists(userDescriptor.getUserID(), storyID, request.story, request.privacy);

        if (exists) return SingleMessageResponse.OK;
        else throw SingleErrorMessageException.DOES_NOT_EXIST;
    }

    @PutMapping("/{storyID}/privacy")
    public SingleMessageResponse updateStoryPrivacy(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable Long storyID,
            @RequestBody @Valid UpdateStoryPrivacyRequest request
    ) {
        boolean exists = storyRepository.updatePrivacyIfExists(userDescriptor.getUserID(), storyID, request.privacy);

        if (exists) return SingleMessageResponse.OK;
        else throw SingleErrorMessageException.DOES_NOT_EXIST;
    }

    @DeleteMapping("/{storyID}")
    public SingleMessageResponse deleteStory(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable Long storyID
    ) {
        boolean exists = storyRepository.deleteIfExists(userDescriptor.getUserID(), storyID);
        if (exists) return SingleMessageResponse.OK;
        else throw SingleErrorMessageException.DOES_NOT_EXIST;
    }
}
