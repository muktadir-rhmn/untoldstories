package me.untoldstories.be.story.repos;

import me.untoldstories.be.utils.Assertion;
import me.untoldstories.be.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

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

    public int fetchNumOfLikes(long storyID) {
        String sql = "SELECT COUNT(id) FROM usersReactToStories WHERE storyID=?";

        return jdbcTemplate.queryForObject(sql, Integer.class, storyID);
    }

    public Map<Long, Integer> fetchNumOfLikes(String storyIDList) {
        String sql = new StringBuilder("SELECT storyID, COUNT(id) AS nLikes FROM usersReactToStories WHERE storyID IN (")
                .append(storyIDList)
                .append(") GROUP BY storyID;")
                .toString();

        Map<Long, Integer> nLikesMap = new HashMap<>();
        jdbcTemplate.query(sql, resultSet -> {
            nLikesMap.put(resultSet.getLong("storyID"), resultSet.getInt("nLikes"));
        });
        return nLikesMap;
    }

    public byte fetchReactionOfUser(long storyID, Long requestingUserID) {
        String sql = "SELECT reaction FROM usersReactToStories WHERE storyID=? AND userID=?";

        try {
            return jdbcTemplate.queryForObject(sql, Byte.class, storyID, requestingUserID);
        } catch (EmptyResultDataAccessException ex) {
            return 0;
        }
    }

    public Map<Long, Byte> fetchReactionsOfUser(String storyIDList, Long requestingUserID) {
        String sql = new StringBuilder("SELECT storyID, reaction FROM usersReactToStories WHERE storyID IN (")
                .append(storyIDList)
                .append(") AND userID=?;")
                .toString();

        Map<Long, Byte> reactionMap = new HashMap<>();
        jdbcTemplate.query(sql, resultSet -> {
            reactionMap.put(resultSet.getLong("storyID"), resultSet.getByte("reaction"));
        }, requestingUserID);
        return reactionMap;
    }
}
