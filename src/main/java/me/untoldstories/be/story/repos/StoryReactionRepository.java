package me.untoldstories.be.story.repos;

import me.untoldstories.be.utils.Assertion;
import me.untoldstories.be.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoryReactionRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StoryReactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void addAndRemovePreviousOnes(long userID, long storyID, byte reaction) {
        removeIfExists(userID, storyID);

        String sql = "INSERT INTO usersReactToStories(userID, storyID, reaction, cTime) VALUES(?, ?, ?, ?)";
        int nRows = jdbcTemplate.update(sql, userID, storyID, reaction, Time.curUnixEpoch());

        Assertion.assertEqual(nRows, 1);
    }

    public boolean removeIfExists(long userID, long storyID) {
        String sql = "DELETE FROM usersReactToStories WHERE userID=? AND storyID=?";
        return jdbcTemplate.update(sql, userID, storyID) > 0;
    }
}
