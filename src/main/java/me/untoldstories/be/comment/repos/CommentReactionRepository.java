package me.untoldstories.be.comment.repos;

import me.untoldstories.be.utils.Assertion;
import me.untoldstories.be.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}