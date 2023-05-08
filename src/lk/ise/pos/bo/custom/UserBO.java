package lk.ise.pos.bo.custom;

import lk.ise.pos.bo.SuperBO;
import lk.ise.pos.dto.UserDto;

import java.sql.SQLException;

public interface UserBO extends SuperBO {
    public void initializeUsers() throws SQLException, ClassNotFoundException;
    public UserDto findUser(String username) throws Exception;
}
