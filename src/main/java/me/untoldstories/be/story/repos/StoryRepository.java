package me.untoldstories.be.story.repos;

import me.untoldstories.be.constants.StoryPrivacy;
import me.untoldstories.be.story.dtos.Story;
import me.untoldstories.be.utils.Assertion;
import me.untoldstories.be.utils.DatabaseUtils;
import me.untoldstories.be.utils.Time;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class StoryRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Long add(
            long userID,
            String story,
            @Range(min = StoryPrivacy.LOWEST_VALUE, max = StoryPrivacy.HIGHEST_VALUE) int privacy
    ) {
        String sql = "INSERT INTO stories(userID, body, privacy, cTime, mTime) VALUES(?, ?, ?, ?, ?);";

        long curTime = Time.curUnixEpoch();
        int nRowsInserted = jdbcTemplate.update(sql, userID, story, privacy, curTime, curTime);
        Assertion.assertEqual(nRowsInserted, 1);

        sql = "SELECT LAST_INSERT_ID();";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public boolean updateIfExists(
            long userID,
            Long storyID,
            String story,
            @Range(min = StoryPrivacy.LOWEST_VALUE, max = StoryPrivacy.HIGHEST_VALUE) int privacy
    ) {
        String sql = "UPDATE stories SET body = ?, privacy=?, mTime = ? WHERE id=? AND userID = ?;";

        long curTime = Time.curUnixEpoch();
        return jdbcTemplate.update(sql, story, privacy, curTime, storyID, userID) == 1;
    }

    public boolean updatePrivacyIfExists(long userID, Long storyID, int privacy) {
        String sql = "UPDATE stories SET privacy=?, mTime = ? WHERE id=? AND userID = ?;";

        long curTime = Time.curUnixEpoch();
        return jdbcTemplate.update(sql, privacy, curTime, storyID, userID) == 1;
    }

    public boolean deleteIfExists(long userID, Long storyID) {
        String sql = "DELETE FROM stories WHERE id=? AND userID=?;";

        return jdbcTemplate.update(sql, storyID, userID) == 1;
    }

    public Story fetchStoryByID(long storyID, long requestingUserID) {
        String sql = "SELECT " +
                Story.getColumnNameList() +
                " FROM stories WHERE id = ? AND (privacy=? OR userID=?);";
        try {
            return jdbcTemplate.queryForObject(sql, Story.getRowMapper(), storyID, StoryPrivacy.PUBLIC, requestingUserID);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    public List<Story> fetchPublicStoriesByStoryIDs(List<Long> storyIDs) {
        if (storyIDs.size() == 0) return Collections.emptyList();

        String sqlSetString = DatabaseUtils.makeStringList(storyIDs);
        String sql = "SELECT " +
                Story.getColumnNameList() +
                " FROM stories WHERE id IN " +
                sqlSetString +
                " AND privacy=? ;";

        return jdbcTemplate.query(sql, Story.getRowMapper(), StoryPrivacy.PUBLIC);
    }

    public List<Story> fetchStoriesByUserID(long userID, int pageNo, int pageSize, long requestingUserID) {
        StringBuilder sql = new StringBuilder("SELECT ")
                .append(Story.getColumnNameList())
                .append(" FROM stories WHERE userID=? ");
        if (userID == requestingUserID) {
            sql.append(" ORDER BY cTime LIMIT ?, ?");
            return jdbcTemplate.query(sql.toString(), Story.getRowMapper(), userID, pageNo * pageSize, pageSize);
        } else {
            sql.append(" AND privacy=?").append(" ORDER BY cTime LIMIT ?, ?");
            return jdbcTemplate.query(sql.toString(), Story.getRowMapper(), userID, StoryPrivacy.PUBLIC, pageNo * pageSize, pageSize);
        }
    }
}
