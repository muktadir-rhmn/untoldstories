package me.untoldstories.be.reply.repos;

import me.untoldstories.be.utils.Assertion;
import me.untoldstories.be.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ReplyReactionRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReplyReactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void addAndRemovePreviousOnes(long userID, long commentID, long replyID, byte reaction) {
        removeIfExists(userID, replyID);

        String sql = "INSERT INTO usersReactToReplies(userID, commentID, replyID, reaction, cTime) VALUES(?, ?, ?, ?, ?)";
        int nRows = jdbcTemplate.update(sql, userID, commentID, replyID, reaction, Time.curUnixEpoch());

        Assertion.assertEqual(nRows, 1);
    }

    public boolean removeIfExists(long userID, long replyID) {
        String sql = "DELETE FROM usersReactToReplies WHERE userID=? AND replyID=?";
        return jdbcTemplate.update(sql, userID, replyID) > 0;
    }

    public Map<Long,int[]> fetchReactions(String replyIDList) {
        String sql = new StringBuilder("SELECT replyID, reaction, COUNT(id) AS nReactions FROM usersReactToReplies WHERE replyID in (")
                .append(replyIDList)
                .append(") GROUP BY replyID, reaction;")
                .toString();

        Map<Long, int[]> replyReactionMap = new HashMap<>();
        jdbcTemplate.query(sql, resultSet -> {
            long replyID = resultSet.getLong("replyID");
            int[] nReactions = replyReactionMap.computeIfAbsent(replyID, k -> new int[]{0, 0});
            nReactions[resultSet.getByte("reaction") - 1] = resultSet.getInt("nReactions");
        });

        return replyReactionMap;
    }

    public Map<Long, Byte> fetchUserReaction(String replyIDList, long requestingUserID) {
        String sql = new StringBuilder("SELECT replyID, reaction FROM usersReactToReplies WHERE userID=? AND replyID IN (")
                .append(replyIDList)
                .append(')')
                .toString();

        Map<Long, Byte> userReactionMap = new HashMap<>();
        jdbcTemplate.query(sql, resultSet -> {
            userReactionMap.put(resultSet.getLong("replyID"), resultSet.getByte("reaction"));
        }, requestingUserID);
        return userReactionMap;
    }
}
