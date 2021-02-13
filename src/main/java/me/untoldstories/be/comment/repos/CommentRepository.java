package me.untoldstories.be.comment.repos;

import me.untoldstories.be.error.exceptions.InternalServerErrorException;
import me.untoldstories.be.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Min;

@Service
public class CommentRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //todo: how to handle foreign key constraint efficiently? or just ignore?
    @Transactional
    public Long add(long userID, String comment, @Min(value = 0, message = "Invalid storyID") Long storyID) {
        String sql = "INSERT INTO comments(userID, storyID, body, cTime, mTime) VALUES(?, ?, ?, ?, ?);";

        long curTime = Time.curUnixEpoch();
        int nRowsInserted = jdbcTemplate.update(sql, userID, storyID, comment, curTime, curTime);
        if (nRowsInserted < 1) throw InternalServerErrorException.EMPTY_EXCEPTION;

        sql = "SELECT LAST_INSERT_ID();";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public boolean updateIfExists(long userID, long commentID, String comment) {
        String sql = "UPDATE comments SET body = ?, mTime = ? WHERE id=? AND userID = ?;";

        long curTime = Time.curUnixEpoch();
        return jdbcTemplate.update(sql, comment, curTime, commentID, userID) == 1;
    }

    public boolean deleteIfExists(long userID, long commentID) {
        String sql = "DELETE FROM comments WHERE id=? AND userID=?;";

        return jdbcTemplate.update(sql, commentID, userID) == 1;
    }
}
