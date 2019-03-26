package com.nemge.ppe.Local;

import com.nemge.ppe.Database.IUserDataSource;
import com.nemge.ppe.Model.User;
import io.reactivex.Flowable;

import java.util.Date;
import java.util.List;

public class UserDataSource implements IUserDataSource {

    private UserDAO userDAO;
    private static UserDataSource mInstance;

    public UserDataSource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public static UserDataSource getInstance(UserDAO userDAO) {
        if (mInstance == null) {
            mInstance = new UserDataSource(userDAO);
        }
        return mInstance;
    }

    @Override
    public Flowable<User> getUserById(int userId) {
        return userDAO.getUserById(userId);
    }

    @Override
    public Flowable<List<User>> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @Override
    public void insertUser(User... users) {
        userDAO.insertUser(users);
    }

    @Override
    public void updateUser(User... users) {
        userDAO.updateUser(users);
    }

    @Override
    public void deleteUser(User user) {
        userDAO.deleteUser(user);
    }

    @Override
    public void deleteAllUsers() {
        userDAO.deleteAllUsers();
    }
    /*
    @Override
    public void insertTime(Date... dose) { userDAO.insertTime(dose);}

    @Override
    public Flowable<User> countBetweenDate(String from, String to) {return userDAO.countBetweenDate(from, to);}
    */
}
