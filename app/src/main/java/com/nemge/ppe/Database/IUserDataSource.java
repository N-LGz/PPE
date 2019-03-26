package com.nemge.ppe.Database;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.nemge.ppe.Model.User;
import io.reactivex.Flowable;

import java.util.Date;
import java.util.List;

public interface IUserDataSource {
    Flowable<User> getUserById(int userId);
    Flowable<List<User>> getAllUsers();
    void insertUser(User... users);
    void updateUser(User... users);
    void deleteUser(User user);
    void deleteAllUsers();
    /*void insertTime(Date... dose);
    Flowable<User> countBetweenDate(String from, String to);*/
}
