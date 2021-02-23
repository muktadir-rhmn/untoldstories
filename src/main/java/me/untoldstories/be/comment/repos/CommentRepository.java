package me.untoldstories.be.comment.repos;

import me.untoldstories.be.comment.pojos.Comment;
import me.untoldstories.be.error.exceptions.InternalServerErrorException;
import me.untoldstories.be.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommentRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //todo: how to handle foreign key constraint efficiently? or just ignore?
    @Transactional
    public Long add(long userID, String body, @Min(value = 0, message = "Invalid storyID") Long storyID) {
        String sql = "INSERT INTO comments(userID, storyID, body, cTime, mTime) VALUES(?, ?, ?, ?, ?);";

        long curTime = Time.curUnixEpoch();
        int nRowsInserted = jdbcTemplate.update(sql, userID, storyID, body, curTime, curTime);
        if (nRowsInserted < 1) throw InternalServerErrorException.EMPTY_EXCEPTION;

        sql = "SELECT LAST_INSERT_ID();";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public boolean updateIfExists(long userID, long commentID, String body) {
        String sql = "UPDATE comments SET body = ?, mTime = ? WHERE id=? AND userID = ?;";

        long curTime = Time.curUnixEpoch();
        return jdbcTemplate.update(sql, body, curTime, commentID, userID) == 1;
    }

    public boolean deleteIfExists(long userID, long commentID) {
        String sql = "DELETE FROM comments WHERE id=? AND userID=?;";

        return jdbcTemplate.update(sql, commentID, userID) == 1;
    }

    public int fetchNumOfCommentsOfStory(long storyID) {
        String sql = "SELECT COUNT(id) FROM comments WHERE storyID=?;";

        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, storyID);
        } catch (EmptyResultDataAccessException exception) {
            return 0;
        }
    }

    public Map<Long, Integer> fetchNumOfCommentsOfStory(String storyIDList) {
        String sql = new StringBuilder("SELECT storyID, COUNT(id) AS nComments FROM comments WHERE storyID in (")
                .append(storyIDList)
                .append(") GROUP BY storyID;")
                .toString();

        Map<Long, Integer> nCommentsMap = new HashMap<>();
        jdbcTemplate.query(sql, resultSet -> {
            nCommentsMap.put(resultSet.getLong("storyID"), resultSet.getInt("nComments"));
        });
        return nCommentsMap;
    }

    public List<Comment> fetchCommentsOfStory(long storyID) {
        String sql = new StringBuilder("SELECT ")
                .append(Comment.getColumnNameList())
                .append(" FROM comments WHERE storyID=? ORDER BY cTime DESC;")
                .toString();

        return jdbcTemplate.query(sql, Comment.getRowMapper(), storyID);
    }
}
