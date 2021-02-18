package me.untoldstories.be.reply.pojos;

import me.untoldstories.be.user.pojos.User;
import org.springframework.jdbc.core.RowMapper;

public class Reply {
    public long id;
    public User author = new User();
    public long commentID;
    public String body;

    public int nLikes;
    public int nDislikes;
    public byte myReaction;

    public long cTime;
    public long mTime;

    public static String getColumnNameList() {
        return "id, userID, commentID, body, cTime, mTime";
    }

    private static final RowMapper<Reply> rowMapper = ((resultSet, i) -> {
        Reply reply = new Reply();

        reply.id = resultSet.getLong("id");
        reply.author.id = resultSet.getLong("userID");
        reply.commentID = resultSet.getLong("commentID");
        reply.body = resultSet.getString("body");
        reply.cTime = resultSet.getLong("cTime");
        reply.mTime = resultSet.getLong("mTime");

        return reply;
    });

    public static RowMapper<Reply> getRowMapper() {
        return rowMapper;
    }
}
