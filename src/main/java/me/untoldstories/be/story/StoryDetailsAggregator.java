package me.untoldstories.be.story;

import me.untoldstories.be.comment.CommentInternalAPI;
import me.untoldstories.be.story.dtos.Story;
import me.untoldstories.be.story.repos.StoryReactionRepository;
import me.untoldstories.be.story.repos.StoryRepository;
import me.untoldstories.be.user.UserInternalAPI;
import me.untoldstories.be.user.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StoryDetailsAggregator {
    private final StoryRepository storyRepository;
    private final StoryReactionRepository storyReactionRepository;
    private final UserInternalAPI userInternalAPI;
    private final CommentInternalAPI commentInternalAPI;

    @Autowired
    public StoryDetailsAggregator(
            StoryRepository storyRepository,
            StoryReactionRepository storyReactionRepository,
            UserInternalAPI userInternalAPI,
            CommentInternalAPI commentInternalAPI
    ) {
        this.storyRepository = storyRepository;
        this.storyReactionRepository = storyReactionRepository;
        this.userInternalAPI = userInternalAPI;
        this.commentInternalAPI = commentInternalAPI;
    }

    public Story fetchStoryByID(long storyID, Long requestingUserID) {
        Story story = storyRepository.fetchStoryByID(storyID, requestingUserID);

        if (story == null) return null;

        story.author.userName = userInternalAPI.fetchUserNameByID(story.author.id).userName;
        story.nLikes = storyReactionRepository.fetchNumOfLikes(story.id);
        story.nComments = commentInternalAPI.fetchNumOfCommentsOfStory(story.id);
        story.myReaction = storyReactionRepository.fetchReactionOfUser(story.id, requestingUserID);

        return story;
    }

    public List<Story> fetchStoriesByIDs(List<Long> storyIDs, Long requestingUserID) {
        List<Story> stories = storyRepository.fetchPublicStoriesByStoryIDs(storyIDs);
        populateRemainingFields(stories, requestingUserID);
        return stories;
    }

    public List<Story> fetchStoriesOfUser(long userID, int pageNo, int pageSize, Long requestingUserID) {
        List<Story> stories = storyRepository.fetchStoriesByUserID(userID, pageNo, pageSize, requestingUserID);
        if (stories.size() == 0) return stories;
        populateRemainingFields(stories, requestingUserID);
        return stories;
    }

    private void populateRemainingFields(List<Story> stories, Long requestingUserID) {
        if (stories.size() == 0) return;

        StringBuilder sbStoryIDList = new StringBuilder();
        StringBuilder sbUserIDList = new StringBuilder();
        for (Story story : stories) {
            sbStoryIDList.append(story.id).append(',');
            sbUserIDList.append(story.author.id).append(',');
        }
        String storyIDList = sbStoryIDList.substring(0, sbStoryIDList.length() - 1);
        String userIDList = sbUserIDList.substring(0, sbUserIDList.length() - 1);

        Map<Long, User> users = userInternalAPI.fetchUsersByIDs(userIDList);
        Map<Long, Integer> nLikes = storyReactionRepository.fetchNumOfLikes(storyIDList);
        Map<Long, Byte> myReactions = storyReactionRepository.fetchReactionsOfUser(storyIDList, requestingUserID);
        Map<Long, Integer> nComments = commentInternalAPI.fetchNumOfCommentsOfStories(storyIDList);

        for (Story story: stories) {
            story.author.userName = users.get(story.author.id).userName;
            story.nLikes = nLikes.getOrDefault(story.id, 0);
            story.nComments = nComments.getOrDefault(story.id, 0);
            story.myReaction = myReactions.getOrDefault(story.id, (byte)0);
        }
    }
}
