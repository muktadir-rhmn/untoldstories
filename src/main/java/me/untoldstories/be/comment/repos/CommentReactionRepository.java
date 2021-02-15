package me.untoldstories.be.comment.repos;

import me.untoldstories.be.utils.Assertion;
import me.untoldstories.be.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class CommentReactionRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CommentReactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void addAndRemovePreviousOnes(long userID, long storyID, long commentID, byte reaction) {
        removeIfExists(userID, commentID);

        String sql = "INSERT INTO usersReactToComments(userID, storyID, commentID, reaction, cTime) VALUES(?, ?, ?, ?, ?)";
        int nRows = jdbcTemplate.update(sql, userID, storyID, commentID, reaction, Time.curUnixEpoch());

        Assertion.assertEqual(nRows, 1);
    }

    public boolean removeIfExists(long userID, long commentID) {
        String sql = "DELETE FROM usersReactToComments WHERE userID=? AND commentID=?";
        return jdbcTemplate.update(sql, userID, commentID) > 0;
    }

    public Map<Long,int[]> fetchReactions(long storyID) {
        String sql = "SELECT commentID, reaction, COUNT(id) AS nReactions FROM usersReactToComments WHERE storyID=? GROUP BY commentID, reaction;";

        Map<Long, int[]> commentReactionMap = new HashMap<>();
        jdbcTemplate.query(sql, resultSet -> {
            long commentID = resultSet.getLong("commentID");
            int[] nReactions = commentReactionMap.get(commentID);
            if (nReactions == null) {
                nReactions = new int[]{0, 0};
                commentReactionMap.put(commentID, nReactions);
            }
            nReactions[resultSet.getByte("reaction") - 1] = resultSet.getInt("nReactions");
        }, storyID);

        return commentReactionMap;
    }

    public Map<Long, Byte> fetchUserReactions(long storyID, long requestingUserID) {
        String sql = "SELECT commentID, reaction FROM usersReactToComments WHERE userID=? AND storyID=?;";

        Map<Long, Byte> userReactionMap = new HashMap<>();
        jdbcTemplate.query(sql, resultSet -> {
            userReactionMap.put(resultSet.getLong("commentID"), resultSet.getByte("reaction"));
        }, requestingUserID, storyID);
        return userReactionMap;
    }
}