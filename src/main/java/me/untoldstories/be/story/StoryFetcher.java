package me.untoldstories.be.story;

import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.story.pojos.Story;
import me.untoldstories.be.story.repos.StoryRepository;
import me.untoldstories.be.user.auth.pojos.SignedInUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static me.untoldstories.be.story.MetaData.STORY_SERVICE_API_BASE_PATH;

class FetchStoriesResponse {
    public List<Story> stories;
}

@RestController
@RequestMapping(STORY_SERVICE_API_BASE_PATH)
public class StoryFetcher {
    private final StoryRepository storyRepository;
    private final StoryDetailsAggregator storyDetailsAggregator;

    @Autowired
    public StoryFetcher(
            StoryRepository storyRepository,
            StoryDetailsAggregator storyDetailsAggregator
    ) {
        this.storyRepository = storyRepository;
        this.storyDetailsAggregator = storyDetailsAggregator;
    }

    @GetMapping("/{storyID}")
    public Story fetchThisStory(
            @RequestAttribute("user") SignedInUser signedInUser,
            @PathVariable long storyID
    ) {
        Story story = storyRepository.fetchStoryByID(storyID, signedInUser.getUserID());
        if (story == null) throw SingleErrorMessageException.DOES_NOT_EXIST;

        storyDetailsAggregator.fillUpAuthorLikeComment(story);
        storyDetailsAggregator.fillUpUserReaction(story, signedInUser.getUserID());
        return story;
    }

    @GetMapping("")
    public FetchStoriesResponse fetchStoriesOfUser (
            @RequestAttribute("user") SignedInUser signedInUser,
            @RequestParam long userID,
            @RequestParam int pageNo,
            @RequestParam int pageSize
    ) {
        if (pageNo < 0 || pageNo > 50 || pageSize > 50 || pageSize < 0) throw SingleErrorMessageException.DOES_NOT_EXIST;

        FetchStoriesResponse response = new FetchStoriesResponse();
        response.stories = storyRepository.fetchStoriesByUserID(userID, pageNo, pageSize, signedInUser.getUserID());
        if (response.stories.size() == 0) return response;

        storyDetailsAggregator.fillUpAuthorLikeComment(response.stories);
        storyDetailsAggregator.fillUpUserReactions(response.stories, signedInUser.getUserID());
        return response;
    }

}
