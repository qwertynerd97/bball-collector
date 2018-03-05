package com.example.app.baseballmessenger;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by pr4h6n on 3/2/18.
 */


/*
* Local Database classes
*   AppDatabase - local database
*   Card - 'CARD' table
*   CardDAO - used to interact with database
*/

@Database(entities = {Card.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CardDAO cardDAO();
}
