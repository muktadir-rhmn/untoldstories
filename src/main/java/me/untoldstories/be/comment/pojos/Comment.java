package me.untoldstories.be.comment.pojos;

import me.untoldstories.be.user.pojos.User;
import org.springframework.jdbc.core.RowMapper;

public class Comment {
    public long id;
    public User author = new User();
    public String body;

    public int nLikes;
    public int nDislikes;
    public byte myReaction;

    public long cTime;
    public long mTime;

    public static String getColumnNameList() {
        return "id, userID, body, cTime, mTime";
    }

    private static final RowMapper<Comment> rowMapper = (resultSet, i) -> {
        Comment comment = new Comment();

        comment.id = resultSet.getLong("id");
        comment.author.id = resultSet.getLong("userID");
        comment.body = resultSet.getString("body");
        comment.cTime = resultSet.getLong("cTime");
        comment.mTime = resultSet.getLong("mTime");

        return comment;
    };

    public static RowMapper<Comment> getRowMapper() {
        return rowMapper;
    }

}
