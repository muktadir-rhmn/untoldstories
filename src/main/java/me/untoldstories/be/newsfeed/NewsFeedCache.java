package me.untoldstories.be.newsfeed;

import me.untoldstories.be.story.StoryInternalAPI;
import me.untoldstories.be.story.dtos.Story;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static me.untoldstories.be.newsfeed.MetaData.PAGE_SIZE;

@Service
public class NewsFeedCache {
    private final static int BUCKET_SIZE = 50;
    private final static int SCHEDULER_INTERVAL_IN_MIN = 1;

    private final Story[][] cachedStoryPageBucket = new Story[BUCKET_SIZE][PAGE_SIZE];
    private Story[] lastBucket; //to avoid sending null as response
    private int nBuckets;
    private final StoryInternalAPI storyInternalAPI;

    @Autowired
    public NewsFeedCache(StoryInternalAPI storyInternalAPI) {
        this.storyInternalAPI = storyInternalAPI;
    }

    private final Story[] EMPTY_BUCKET = new Story[0];
    public Story[] fetchStories(int pageNo, long userID) {
        if (pageNo < 0 || pageNo >= nBuckets) return EMPTY_BUCKET;

        Story[] stories = pageNo == nBuckets - 1 ? lastBucket : cachedStoryPageBucket[pageNo];

        //todo: handle nLikes, nComments change due to cache. Now it is updated every minute.
        storyInternalAPI.fillUpUserReactions(Arrays.asList(stories), userID);
        return stories;
    }

    @Scheduled(fixedRate = SCHEDULER_INTERVAL_IN_MIN * 60000)
    public void scheduledTask() {
        System.out.println("ScheduledTask: Going to fetch from Story Module");
        List<Story> recentStories = storyInternalAPI.fetchRecentPublicStories(0, BUCKET_SIZE * PAGE_SIZE);

        int r = 0;
        int c = 0;
        for (Story story: recentStories) {
            cachedStoryPageBucket[r][c] = story;
            c++;
            if (c == PAGE_SIZE) {
                r++;
                c = 0;
            }
        }
        nBuckets = r + (c > 0 ? 1 : 0);

        int t = recentStories.size() % PAGE_SIZE;
        int lastBucketSize = (t == 0 ? PAGE_SIZE : t);
        lastBucket = new Story[lastBucketSize];

        r = nBuckets - 1;
        for (c = 0; c < lastBucketSize; c++) {
            lastBucket[c] = cachedStoryPageBucket[r][c];
        }

    }
}
