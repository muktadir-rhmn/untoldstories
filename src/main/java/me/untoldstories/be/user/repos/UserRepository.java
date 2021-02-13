package me.untoldstories.be.user.repos;

import me.untoldstories.be.user.dtos.User;
import me.untoldstories.be.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
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

	public User getUserByUserName(String userName) {
		String sql = "SELECT id, userName, password, cTime, mTime FROM users WHERE userName=?";

		try {
			return jdbcTemplate.queryForObject(sql,
					(rs, rowNum) -> new User(rs.getLong("id"), rs.getString("userName"), rs.getString("password"), rs.getLong("cTime"), rs.getLong("mTime")),
					userName
			);
		} catch (EmptyResultDataAccessException exception) {
			return null;
		}
	}
}
