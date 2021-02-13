package me.untoldstories.be.reply.repos;

import me.untoldstories.be.utils.Assertion;
import me.untoldstories.be.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
}
