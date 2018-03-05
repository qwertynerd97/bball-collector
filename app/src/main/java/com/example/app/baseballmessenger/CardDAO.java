package com.example.app.baseballmessenger;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by pr4h6n on 3/2/18.
 */

/*
* Local Database classes
*   AppDatabase - local database
*   Card - 'CARD' table
*   CardDAO - used to interact with database
*/

@Dao
public interface CardDAO {

    @Query("SELECT * FROM cards")
    List<Card> getAll();

    @Query("SELECT * FROM cards WHERE NAME LIKE :name LIMIT 1")
    Card findByName(String name);

    @Query("DELETE FROM cards")
    public void nukeTable();

    @Insert
    void insertAll(Card... cards);

    @Query("DELETE FROM cards WHERE NAME LIKE :name AND NUMBER LIKE :number")
    void delete(String name, String number);

}
