package com.nemge.ppe.Database;

import com.nemge.ppe.Model.User;
import io.reactivex.Flowable;

import java.util.Date;
import java.util.List;

public class UserRepository implements IUserDataSource {

    private IUserDataSource mLocalDataSource;
    private static UserRepository mInstance;

    public UserRepository(IUserDataSource mLocalDataSource) {
        this.mLocalDataSource = mLocalDataSource;
    }

    public static UserRepository getInstance(IUserDataSource mLocalDataSource)
    {
        if (mInstance == null) {
            mInstance = new UserRepository(mLocalDataSource);
        }
        return mInstance;
    }

    @Override
    public Flowable<User> getUserById(int userId) {
        return mLocalDataSource.getUserById(userId);
    }

    @Override
    public Flowable<List<User>> getAllUsers() {
        return mLocalDataSource.getAllUsers();
    }

    @Override
    public void insertUser(User... users) {
        mLocalDataSource.insertUser(users);
    }

    @Override
    public void updateUser(User... users) {
        mLocalDataSource.updateUser(users);
    }

    @Override
    public void deleteUser(User user) {
        mLocalDataSource.deleteUser(user);
    }

    @Override
    public void deleteAllUsers() {
        mLocalDataSource.deleteAllUsers();
    }
    /*
    @Override
    public void insertTime(Date... dose) { mLocalDataSource.insertTime(dose);}

    @Override
    public Flowable<User> countBetweenDate(String from, String to) {return mLocalDataSource.countBetweenDate(from, to);}
    */
}
