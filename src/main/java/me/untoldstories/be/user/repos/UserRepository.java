package me.untoldstories.be.user.repos;

import me.untoldstories.be.user.entities.UserEntity;
import me.untoldstories.be.user.pojos.User;
import me.untoldstories.be.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public final class UserRepository {
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public UserRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean createUserIfNotExists(String userName, String password) {
		String sql = "INSERT INTO users(userName, password, cTime, mTime) VALUES(?, ?, ?, ?)";

		try {
			long curTime = Time.curUnixEpoch();
			jdbcTemplate.update(sql, userName, password, curTime, curTime);
			return true;
		} catch (DuplicateKeyException ex) {
			return false;
		}
	}

	public UserEntity getUserEntityByUserName(String userName) {
		String sql = "SELECT id, userName, password, cTime, mTime FROM users WHERE userName=?";

		try {
			return jdbcTemplate.queryForObject(sql,
					(rs, rowNum) -> new UserEntity(rs.getLong("id"), rs.getString("userName"), rs.getString("password"), rs.getLong("cTime"), rs.getLong("mTime")),
					userName
			);
		} catch (EmptyResultDataAccessException exception) {
			return null;
		}
	}

	public User fetchUserByUserID(long userID) {
		User user = new User();
		String sql = "SELECT userName FROM users WHERE id=?";

		user.id = userID;
		try {
			user.userName = jdbcTemplate.queryForObject(sql, String.class, userID);
			return user;
		} catch (EmptyResultDataAccessException exception) {
			return null;
		}
	}

	public Map<Long, User> fetchUserNamesByIDs(String userIDList) {
		String sql = new StringBuilder("SELECT id, userName FROM users WHERE id IN (")
				.append(userIDList)
				.append(')')
				.toString();

		Map<Long, User> userMap = new HashMap<>();
		jdbcTemplate.query(sql, resultSet -> {
			User user = new User();
			user.id = resultSet.getLong("id");
			user.userName = resultSet.getString("userName");
			userMap.put(user.id, user);
		});
		return userMap;
	}
}
