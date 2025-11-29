package com.ivzb.urgat.data.source.local

import android.content.Context
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ivzb.urgat.data.investment.Investment
import com.ivzb.urgat.data.investment.InvestmentEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        val allMigrations = arrayOf(
            MIGRATION_1_2,
        )

        // Create initial version of the database.
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }

        // Open latest version of the database. Room validates the schema
        // once all migrations execute.
        Room
            .databaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                AppDatabase::class.java,
                TEST_DB
            )
            .addMigrations(*allMigrations)
            .build()
            .apply {
                openHelper.writableDatabase.close()
            }
    }

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        helper.createDatabase(TEST_DB, 1)
            .let { db ->
                // Database has schema version 1. Insert some data using SQL queries.
                // DAO classes can't be used here because they expect the latest schema.
                db.execSQL("INSERT INTO investments (`id`, `name`, `currency`, `amount`, `category`, `isDeleted`, `createdAt`) VALUES ('1', 'SNP 500', 'USD', 500, 'ETF', 0, 1764362076)")
                db.close()
            }

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2).close()

        // verifications
        Room.databaseBuilder(context, AppDatabase::class.java, TEST_DB)
            .allowMainThreadQueries()
            .build()
            .let { db ->
                // verify default values after migration
                runBlocking {
                    val actual = db.investmentDao().getById("1")
                    val expected = InvestmentEntity(
                        id = "1",
                        name = "SNP 500",
                        amount = 500.toBigDecimal(),
                        cost = 0.toBigDecimal(),
                        category = "ETF",
                        currency = "USD",
                        date = 0,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expected, actual)
                }

                // update the new fields
                runBlocking {
                    val investment = InvestmentEntity(
                        id = "1",
                        name = "SNP 500",
                        amount = 500.toBigDecimal(),
                        cost = 250.toBigDecimal(),
                        category = "ETF",
                        currency = "USD",
                        date = 1764438110,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    db.investmentDao().upsert(investment)
                }

                // verify new fields are updated
                runBlocking {
                    val actual = db.investmentDao().getById("1")
                    val expected = InvestmentEntity(
                        id = "1",
                        name = "SNP 500",
                        amount = 500.toBigDecimal(),
                        cost = 250.toBigDecimal(),
                        category = "ETF",
                        currency = "USD",
                        date = 1764438110,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expected, actual)
                }

                db.close()
            }
    }

    companion object {

        private const val TEST_DB = "migration-test-db"
    }
}