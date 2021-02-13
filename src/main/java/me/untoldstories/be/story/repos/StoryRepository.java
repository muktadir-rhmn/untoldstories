package me.untoldstories.be.story.repos;

import me.untoldstories.be.error.exceptions.InternalServerErrorException;
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
    public Long add(long userID, String story) {
        String sql = "INSERT INTO stories(userID, body, cTime, mTime) VALUES(?, ?, ?, ?);";

        long curTime = System.currentTimeMillis();
        int nRowsInserted = jdbcTemplate.update(sql, userID, story, curTime, curTime);
        if (nRowsInserted < 1) throw InternalServerErrorException.EMPTY_EXCEPTION;

        sql = "SELECT LAST_INSERT_ID();";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public boolean updateIfExists(long userID, Long storyID, String story) {
        String sql = "UPDATE stories SET body = ?, mTime = ? WHERE id=? AND userID = ?;";

        long curTime = System.currentTimeMillis();
        return jdbcTemplate.update(sql, story, curTime, storyID, userID) == 1;
    }

    public boolean deleteIfExists(long userID, Long storyID) {
        String sql = "DELETE FROM stories WHERE id=? AND userID=?;";

        return jdbcTemplate.update(sql, storyID, userID) == 1;
    }
}
