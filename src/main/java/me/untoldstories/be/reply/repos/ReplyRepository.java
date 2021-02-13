package me.untoldstories.be.reply.repos;

import me.untoldstories.be.error.exceptions.InternalServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReplyRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReplyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Long add(long userID, long storyID, long commentID, String reply) {
        String sql = "INSERT INTO replies(userID, storyID, commentID, body, cTime, mTime) VALUES(?, ?, ?, ?, ?, ?);";

        long curTime = System.currentTimeMillis();
        int nRowsInserted = jdbcTemplate.update(sql, userID, storyID, commentID, reply, curTime, curTime);
        if (nRowsInserted < 1) throw InternalServerErrorException.EMPTY_EXCEPTION;

        sql = "SELECT LAST_INSERT_ID();";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public boolean updateIfExists(long userID, long replyID, String reply) {
        String sql = "UPDATE replies SET body = ?, mTime = ? WHERE id=? AND userID = ?;";

        long curTime = System.currentTimeMillis();
        return jdbcTemplate.update(sql, reply, curTime, replyID, userID) == 1;
    }

    public boolean deleteIfExists(long userID, long replyID) {
        String sql = "DELETE FROM replies WHERE id=? AND userID=?;";

        return jdbcTemplate.update(sql, replyID, userID) == 1;
    }
}
