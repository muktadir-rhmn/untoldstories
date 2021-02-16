package me.untoldstories.be.newsfeed;

import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.story.dtos.Story;
import me.untoldstories.be.user.pojos.UserDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static me.untoldstories.be.newsfeed.MetaData.NEWSFEED_SERVICE_API_ROOT_PATH;

class NewsFeedResponse {
    public Story[] stories;
}

@RestController
@RequestMapping(NEWSFEED_SERVICE_API_ROOT_PATH)
public class NewsFeedFetcher {
    private final NewsFeedCache newsFeedCache;

    @Autowired
    public NewsFeedFetcher(NewsFeedCache newsFeedCache) {
        this.newsFeedCache = newsFeedCache;
    }

    @GetMapping()
    public NewsFeedResponse fetchStories(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @RequestParam int pageNo
    ) {
        if (pageNo < 0 || pageNo > 50) throw SingleErrorMessageException.DOES_NOT_EXIST;

        NewsFeedResponse response = new NewsFeedResponse();
        response.stories = newsFeedCache.fetchStories(pageNo, userDescriptor.getUserID());
        return response;
    }
}
