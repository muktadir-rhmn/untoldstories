package me.untoldstories.be.newsfeed;

import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.story.pojos.Story;
import me.untoldstories.be.user.auth.pojos.SignedInUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static me.untoldstories.be.newsfeed.MetaData.NEWSFEED_SERVICE_API_BASE_PATH;

class NewsFeedResponse {
    public Story[] stories;
}

@RestController
@RequestMapping(NEWSFEED_SERVICE_API_BASE_PATH)
public class NewsFeedFetcher {
    private final NewsFeedCache newsFeedCache;

    @Autowired
    public NewsFeedFetcher(NewsFeedCache newsFeedCache) {
        this.newsFeedCache = newsFeedCache;
    }

    @GetMapping()
    public NewsFeedResponse fetchStories(
            @RequestAttribute("user") SignedInUser signedInUser,
            @RequestParam int pageNo
    ) {
        if (pageNo < 0 || pageNo > 50) throw SingleErrorMessageException.DOES_NOT_EXIST;

        NewsFeedResponse response = new NewsFeedResponse();
        response.stories = newsFeedCache.fetchStories(pageNo, signedInUser.getUserID());
        return response;
    }
}
