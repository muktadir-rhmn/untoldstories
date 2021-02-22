package me.untoldstories.be.story;

import me.untoldstories.be.constants.StoryPrivacy;
import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.story.repos.StoryRepository;
import me.untoldstories.be.user.auth.pojos.SignedInUser;
import me.untoldstories.be.utils.pojos.SingleIDResponse;
import me.untoldstories.be.utils.pojos.SingleMessageResponse;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static me.untoldstories.be.story.MetaData.STORY_SERVICE_API_BASE_PATH;

class AddUpdateStoryRequest {
    @NotBlank(message = "Story must not be empty")
    @Size(max = 5000, message = "Too lengthy")
    public String body;

    @Range(min = StoryPrivacy.LOWEST_VALUE, max = StoryPrivacy.HIGHEST_VALUE)
    public int privacy;
}

class UpdateStoryPrivacyRequest {
    @Range(min = StoryPrivacy.LOWEST_VALUE, max = StoryPrivacy.HIGHEST_VALUE)
    public int privacy;
}

@RestController
@RequestMapping(STORY_SERVICE_API_BASE_PATH)
public final class StoryCUD {
    private final StoryRepository storyRepository;

    @Autowired
    public StoryCUD(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    @PostMapping("")
    public SingleIDResponse addStory(
            @RequestAttribute("user") SignedInUser signedInUser,
            @RequestBody @Valid AddUpdateStoryRequest request
    ) {
        Long storyID = storyRepository.add(signedInUser.getUserID(), request.body, request.privacy);
        return new SingleIDResponse(storyID);
    }

    @PutMapping("/{storyID}")
    public SingleMessageResponse updateStory(
            @RequestAttribute("user") SignedInUser signedInUser,
            @PathVariable Long storyID,
            @RequestBody @Valid AddUpdateStoryRequest request
    ) {
        boolean exists = storyRepository.updateIfExists(signedInUser.getUserID(), storyID, request.body, request.privacy);

        if (exists) return SingleMessageResponse.OK;
        else throw SingleErrorMessageException.DOES_NOT_EXIST;
    }

    @PutMapping("/{storyID}/privacy")
    public SingleMessageResponse updateStoryPrivacy(
            @RequestAttribute("user") SignedInUser signedInUser,
            @PathVariable Long storyID,
            @RequestBody @Valid UpdateStoryPrivacyRequest request
    ) {
        boolean exists = storyRepository.updatePrivacyIfExists(signedInUser.getUserID(), storyID, request.privacy);

        if (exists) return SingleMessageResponse.OK;
        else throw SingleErrorMessageException.DOES_NOT_EXIST;
    }

    @DeleteMapping("/{storyID}")
    public SingleMessageResponse deleteStory(
            @RequestAttribute("user") SignedInUser signedInUser,
            @PathVariable Long storyID
    ) {
        boolean exists = storyRepository.deleteIfExists(signedInUser.getUserID(), storyID);
        if (exists) return SingleMessageResponse.OK;
        else throw SingleErrorMessageException.DOES_NOT_EXIST;
    }
}
