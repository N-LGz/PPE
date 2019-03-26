package com.nemge.ppe.Local;

import android.arch.persistence.room.*;
import com.nemge.ppe.Model.User;
import io.reactivex.Flowable;

import java.util.Date;
import java.util.List;

@Dao
public interface UserDAO {
    @Query("SELECT * FROM users WHERE id=:userId")
    Flowable<User> getUserById(int userId);

    @Query("SELECT * FROM users")
    Flowable<List<User>> getAllUsers();

    @Insert
    void insertUser(User... users);

    @Update
    void updateUser(User... users);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM users")
    void deleteAllUsers();

    /*
    @Insert
    void insertTime(Date... dose);

    @Query("SELECT count(*) FROM users WHERE dose BETWEEN date(:from) AND date(:to)")
    Flowable<User> countBetweenDate(String from, String to);
    */
}
