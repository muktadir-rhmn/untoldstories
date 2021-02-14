package me.untoldstories.be.story.repos;

import me.untoldstories.be.constants.StoryPrivacy;
import me.untoldstories.be.utils.Assertion;
import me.untoldstories.be.utils.Time;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoryRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Long add(
            long userID,
            String story,
            @Range(min = StoryPrivacy.LOWEST_VALUE, max = StoryPrivacy.HIGHEST_VALUE) int privacy
    ) {
        String sql = "INSERT INTO stories(userID, body, privacy, cTime, mTime) VALUES(?, ?, ?, ?, ?);";

        long curTime = Time.curUnixEpoch();
        int nRowsInserted = jdbcTemplate.update(sql, userID, story, privacy, curTime, curTime);
        Assertion.assertEqual(nRowsInserted, 1);

        sql = "SELECT LAST_INSERT_ID();";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public boolean updateIfExists(
            long userID,
            Long storyID,
            String story,
            @Range(min = StoryPrivacy.LOWEST_VALUE, max = StoryPrivacy.HIGHEST_VALUE) int privacy
    ) {
        String sql = "UPDATE stories SET body = ?, privacy=?, mTime = ? WHERE id=? AND userID = ?;";

        long curTime = Time.curUnixEpoch();
        return jdbcTemplate.update(sql, story, privacy, curTime, storyID, userID) == 1;
    }

    public boolean updatePrivacyIfExists(long userID, Long storyID, int privacy) {
        String sql = "UPDATE stories SET privacy=?, mTime = ? WHERE id=? AND userID = ?;";

        long curTime = Time.curUnixEpoch();
        return jdbcTemplate.update(sql, privacy, curTime, storyID, userID) == 1;
    }

    public boolean deleteIfExists(long userID, Long storyID) {
        String sql = "DELETE FROM stories WHERE id=? AND userID=?;";

        return jdbcTemplate.update(sql, storyID, userID) == 1;
    }
}
