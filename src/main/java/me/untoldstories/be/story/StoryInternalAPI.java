package me.untoldstories.be.story;

import me.untoldstories.be.story.dtos.Story;
import me.untoldstories.be.story.repos.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoryInternalAPI {
    private final StoryRepository storyRepository;
    private final StoryDetailsAggregator storyDetailsAggregator;

    @Autowired
    public StoryInternalAPI(
            StoryRepository storyRepository,
            StoryDetailsAggregator storyDetailsAggregator
    ) {
        this.storyRepository = storyRepository;
        this.storyDetailsAggregator = storyDetailsAggregator;
    }

    public List<Story> fetchRecentPublicStories(int pageNo, int pageSize) {
        List<Story> stories = storyRepository.fetchRecentPublicStories(pageNo, pageSize);
        storyDetailsAggregator.fillUpAuthorLikeComment(stories);
        return stories;
    }

    public void fillUpUserReactions(List<Story> stories, long userID) {
        storyDetailsAggregator.fillUpUserReactions(stories, userID);
    }
}
