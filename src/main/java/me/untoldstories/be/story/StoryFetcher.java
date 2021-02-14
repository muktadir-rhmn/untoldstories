package me.untoldstories.be.story;

import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.story.dtos.Story;
import me.untoldstories.be.user.pojos.UserDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static me.untoldstories.be.story.MetaData.STORY_SERVICE_API_ROOT_PATH;

class FetchStoriesResponse {
    public List<Story> stories;

    public FetchStoriesResponse(List<Story> stories) {
        this.stories = stories;
    }
}

@RestController
@RequestMapping(STORY_SERVICE_API_ROOT_PATH)
public class StoryFetcher {
    private final StoryDetailsAggregator storyDetailsAggregator;

    @Autowired
    public StoryFetcher(StoryDetailsAggregator storyDetailsAggregator) {
        this.storyDetailsAggregator = storyDetailsAggregator;
    }

    @GetMapping("/{storyID}")
    public Story fetchThisStory(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable long storyID
    ) {
        Story story = storyDetailsAggregator.fetchStoryByID(storyID, userDescriptor.getUserID());
        if (story == null) throw SingleErrorMessageException.DOES_NOT_EXIST;
        return story;
    }

    @GetMapping("")
    public FetchStoriesResponse fetchStoriesOfUser (
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @RequestParam long userID,
            @RequestParam int pageNo,
            @RequestParam int pageSize
    ) {

        List<Story> stories = storyDetailsAggregator.fetchStoriesOfUser(userID, pageNo, pageSize, userDescriptor.getUserID());
        return new FetchStoriesResponse(stories);
    }

}
