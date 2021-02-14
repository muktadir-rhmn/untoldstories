package me.untoldstories.be.story.dtos;

import me.untoldstories.be.user.pojos.User;
import org.springframework.jdbc.core.RowMapper;


public class Story {
    public long id;
    public User author = new User();
    public String body;
    public byte privacy;

    public long cTime;
    public long mTime;

    public int nViews;
    public int nLikes;
    public int nComments;

    public byte myReaction;


    public static String getColumnNameList() {
        return " id, userID, body, privacy, nViews, mTime, cTime ";
    }

    private static RowMapper<Story> rowMapper = (resultSet, iRow) -> {
        Story story = new Story();

        story.id = resultSet.getLong("id");
        story.author.id = resultSet.getLong("userID");
        story.body = resultSet.getString("body");
        story.privacy = resultSet.getByte("privacy");
        story.nViews = resultSet.getInt("nViews");
        story.cTime = resultSet.getLong("cTime");
        story.mTime = resultSet.getLong("mTime");

        return story;
    };

    public static RowMapper<Story> getRowMapper() {
        return rowMapper;
    }
}
