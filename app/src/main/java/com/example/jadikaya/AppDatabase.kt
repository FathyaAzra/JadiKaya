package com.example.jadikaya

import androidx.room.Database
import androidx.room.RoomDatabase

// Make sure the Transaction entity is properly defined in a separate file.
@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Define an abstract function to get your DAO
    abstract fun transactionDao(): TransactionDao
}
