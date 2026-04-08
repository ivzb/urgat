package com.ivzb.urgat.data.source.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `investments` ADD COLUMN `cost` TEXT NOT NULL DEFAULT '0';")
        db.execSQL("ALTER TABLE `investments` ADD COLUMN `date` INTEGER NOT NULL DEFAULT 0;")
        db.execSQL("UPDATE `investments` SET `date` = `createdAt` WHERE `date` = 0;")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `weights` ADD COLUMN `date` INTEGER NOT NULL DEFAULT 0;")
        db.execSQL("UPDATE `weights` SET `date` = `createdAt` WHERE `date` = 0;")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE `investments_new` (
                `id` TEXT NOT NULL, 
                `name` TEXT NOT NULL, 
                `shares` TEXT NOT NULL,
                `cost` TEXT NOT NULL,
                `category` TEXT NOT NULL,
                `currency` TEXT NOT NULL,
                `date` INTEGER NOT NULL,
                `isDeleted` INTEGER NOT NULL,
                `createdAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)

        db.execSQL("""
            INSERT INTO `investments_new` (`id`, `name`, `shares`, `cost`, `category`, `currency`, `date`, `isDeleted`, `createdAt`)
            SELECT `id`, `name`, `amount`, `cost`, `category`, `currency`, `date`, `isDeleted`, `createdAt` FROM `investments`
        """)

        db.execSQL("DROP TABLE `investments`")

        db.execSQL("ALTER TABLE `investments_new` RENAME TO `investments`")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `expenses` ADD COLUMN `excludeFromAnalytics` INTEGER NOT NULL DEFAULT 0;")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE `budgets` (
                `id` TEXT NOT NULL, 
                `income` TEXT NOT NULL, 
                `bankStart` TEXT NOT NULL, 
                `bankEnd` TEXT, 
                `date` INTEGER NOT NULL, 
                `isDeleted` INTEGER NOT NULL, 
                `createdAt` INTEGER NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """)
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE `savings` (
                `id` TEXT NOT NULL, 
                `name` TEXT NOT NULL, 
                `amount` TEXT NOT NULL, 
                `date` INTEGER NOT NULL, 
                `isDeleted` INTEGER NOT NULL, 
                `createdAt` INTEGER NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """)
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE `todo_new` (
                `id` TEXT NOT NULL, 
                `content` TEXT NOT NULL, 
                `isCompleted` INTEGER NOT NULL, 
                `isDeleted` INTEGER NOT NULL, 
                `createdAt` INTEGER NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """)

        db.execSQL("""
            INSERT INTO `todo_new` (`id`, `content`, `isCompleted`, `isDeleted`, `createdAt`)
            SELECT `id`, `title` || CHAR(10) || `description`, `isCompleted`, `isDeleted`, `createdAt` FROM `todo`
        """)

        db.execSQL("DROP TABLE `todo`")

        db.execSQL("ALTER TABLE `todo_new` RENAME TO `todo`")
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `budgets` ADD COLUMN `currency` TEXT NOT NULL DEFAULT 'EUR';")
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE `watchlist_assets` (
                `id` TEXT NOT NULL, 
                `name` TEXT NOT NULL, 
                `isDeleted` INTEGER NOT NULL, 
                `createdAt` INTEGER NOT NULL, 
                PRIMARY KEY(`id`)
            )
        """)
    }
}

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `savings` ADD COLUMN `currency` TEXT NOT NULL DEFAULT 'EUR';")
    }
}

val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `expenses` ADD COLUMN `currency` TEXT NOT NULL DEFAULT 'EUR';")
    }
}

val ALL_MIGRATIONS = arrayOf(
    MIGRATION_1_2,
    MIGRATION_2_3,
    MIGRATION_3_4,
    MIGRATION_4_5,
    MIGRATION_5_6,
    MIGRATION_6_7,
    MIGRATION_7_8,
    MIGRATION_8_9,
    MIGRATION_9_10,
    MIGRATION_10_11,
    MIGRATION_11_12,
)
