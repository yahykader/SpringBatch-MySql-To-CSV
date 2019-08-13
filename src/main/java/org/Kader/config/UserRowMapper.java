package org.Kader.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.Kader.entities.User;
import org.springframework.jdbc.core.RowMapper;

public class UserRowMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user=new User();
		user.setId(rs.getString("id"));
		user.setFirstName(rs.getString("firstName"));
		user.setLastName(rs.getString("lastName"));
		user.setEmail(rs.getString("email"));
		try {
			user.setAge(rs.getInt("age"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return user;
	}

}
