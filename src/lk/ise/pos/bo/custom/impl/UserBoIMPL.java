package lk.ise.pos.bo.custom.impl;

import lk.ise.pos.bo.custom.UserBO;
import lk.ise.pos.dao.custom.UserDao;
import lk.ise.pos.dao.custom.impl.UserDaoImpl;
import lk.ise.pos.dto.UserDto;
import lk.ise.pos.entity.User;

import java.sql.SQLException;

public class UserBoIMPL implements UserBO {

    UserDao userDao = new UserDaoImpl();
    @Override
    public void initializeUsers() throws SQLException, ClassNotFoundException {
        userDao.initializeUsers();
    }

    @Override
    public UserDto findUser(String username) throws Exception {
        User user = userDao.find(username);
        if (user!=null){
            return new UserDto(
                    user.getUsername(),user.getPassword()
            );
        }
        return null;
    }
}
