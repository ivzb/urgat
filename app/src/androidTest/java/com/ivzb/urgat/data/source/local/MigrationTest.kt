package com.ivzb.urgat.data.source.local

import android.content.Context
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ivzb.urgat.data.carMaintenance.CarMaintenanceEntity
import com.ivzb.urgat.data.expense.ExpenseEntity
import com.ivzb.urgat.data.fuelConsumption.FuelConsumptionEntity
import com.ivzb.urgat.data.investment.InvestmentEntity
import com.ivzb.urgat.data.note.NoteEntity
import com.ivzb.urgat.data.todo.TodoEntity
import com.ivzb.urgat.data.trade.TradeEntity
import com.ivzb.urgat.data.watchlist.WatchlistAssetEntity
import com.ivzb.urgat.data.weight.WeightEntity
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
    fun testMigrationSchemaIntegrity() {
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
            .addMigrations(
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
                MIGRATION_12_13,
                MIGRATION_13_14,
                MIGRATION_14_15,
                MIGRATION_15_16,
                MIGRATION_16_17,
                MIGRATION_17_18,
            )
            .build()
            .apply {
                openHelper.writableDatabase.close()
            }
    }

    @Test
    @Throws(IOException::class)
    fun testMigrationDataIntegrity() {
        // Create initial version of the database.
        helper.createDatabase(TEST_DB, 1)
            .let { db ->
                // Database has schema version 1. Insert some data using SQL queries.
                // DAO classes can't be used here because they expect the latest schema.
                db.execSQL("INSERT INTO `todo` (`id`, `title`, `description`, `isCompleted`, `isDeleted`, `createdAt`) VALUES ('1', 'Buy groceries', 'Milk, eggs, bread, and fruits', 0, 0, 1764362076)")
                db.execSQL("INSERT INTO `expenses` (`id`, `name`, `amount`, `category`, `date`, `isDeleted`, `createdAt`) VALUES ('1', 'Groceries', '75.5', 'Food', 1764362076, 0, 1764362076)")
                db.execSQL("INSERT INTO `investments` (`id`, `name`, `currency`, `amount`, `category`, `isDeleted`, `createdAt`) VALUES ('1', 'SNP 500', 'USD', 500, 'ETF', 0, 1764362076)")
                db.execSQL("INSERT INTO `notes` (`id`, `content`, `isDeleted`, `createdAt`) VALUES ('1', 'Meeting notes about Q4 goals and deadlines', 0, 1764362076)")
                db.execSQL("INSERT INTO `weights` (`id`, `value`, `isDeleted`, `createdAt`) VALUES ('1', '80.5', 0, 1764362076)")
                db.close()
            }

        // Open latest version of the database. Room validates the schema
        // once all migrations execute.
        Room
            .databaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                AppDatabase::class.java,
                TEST_DB
            )
            .addMigrations(
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
                MIGRATION_12_13,
                MIGRATION_13_14,
                MIGRATION_14_15,
                MIGRATION_15_16,
                MIGRATION_16_17,
                MIGRATION_17_18,
            )
            .build()
            .let { db ->
                // verify data integrity after migration
                runBlocking {
                    val actualTodo = db.todoDao().getById("1")
                    val expectedTodo = TodoEntity(
                        id = "1",
                        content = "Buy groceries\nMilk, eggs, bread, and fruits",
                        isCompleted = false,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedTodo, actualTodo)

                    val actualExpense = db.expenseDao().getById("1")
                    val expectedExpense = ExpenseEntity(
                        id = "1",
                        name = "Groceries",
                        amount = 75.5.toBigDecimal(),
                        currency = "EUR",
                        category = "Food",
                        date = 1764362076,
                        excludeFromAnalytics = false,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedExpense, actualExpense)

                    val actualInvestment = db.investmentDao().getById("1")
                    val expectedInvestment = InvestmentEntity(
                        id = "1",
                        name = "SNP 500",
                        shares = 500.toBigDecimal(),
                        cost = 0.toBigDecimal(),
                        category = "ETF",
                        currency = "USD",
                        date = 1764362076,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedInvestment, actualInvestment)

                    val actualNote = db.noteDao().getById("1")
                    val expectedNote = NoteEntity(
                        id = "1",
                        content = "Meeting notes about Q4 goals and deadlines",
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedNote, actualNote)

                    val actualWeight = db.weightDao().getById("1")
                    val expectedWeight = WeightEntity(
                        id = "1",
                        value = 80.5.toBigDecimal(),
                        date = 1764362076,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedWeight, actualWeight)

                    val fuelConsumption = FuelConsumptionEntity(
                        id = "1",
                        amount = 45.0.toBigDecimal(),
                        price = 85.toBigDecimal(),
                        currency = "EUR",
                        type = "Petrol",
                        carName = "Honda Civic",
                        odometer = 50000,
                        date = 1764362076,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    db.fuelConsumptionDao().upsert(fuelConsumption)

                    val actualFuelConsumption = db.fuelConsumptionDao().getById("1")
                    assertEquals(fuelConsumption, actualFuelConsumption)

                    val carMaintenance = CarMaintenanceEntity(
                        id = "1",
                        description = "Oil change",
                        partNumber = "OIL-5W30",
                        url = null,
                        carName = "Honda Civic",
                        odometer = 50000,
                        date = 1764362076,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    db.carMaintenanceDao().upsert(carMaintenance)

                    val actualCarMaintenance = db.carMaintenanceDao().getById("1")
                    assertEquals(carMaintenance, actualCarMaintenance)

                    val trading = TradeEntity(
                        id = "1",
                        name = "AAPL",
                        shares = 10.toBigDecimal(),
                        marketPrice = 150.toBigDecimal(),
                        fee = 1.5.toBigDecimal(),
                        currency = "USD",
                        orderType = "Buy",
                        date = 1764362076,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    db.tradingDao().upsert(trading)

                    val actualTrading = db.tradingDao().getById("1")
                    assertEquals(trading, actualTrading)
                }

                db.openHelper.writableDatabase.close()
            }
    }

    @Test
    @Throws(IOException::class)
    fun testMigrationChain() {
        helper.createDatabase(TEST_DB, 1)
            .let { db ->
                // Database has schema version 1. Insert some data using SQL queries.
                // DAO classes can't be used here because they expect the latest schema.
                db.execSQL("INSERT INTO `todo` (`id`, `title`, `description`, `isCompleted`, `isDeleted`, `createdAt`) VALUES ('1', 'Buy groceries', 'Milk, eggs, bread, and fruits', 0, 0, 1764362076)")
                db.execSQL("INSERT INTO `expenses` (`id`, `name`, `amount`, `category`, `date`, `isDeleted`, `createdAt`) VALUES ('1', 'Groceries', '75.5', 'Food', 1764362076, 0, 1764362076)")
                db.execSQL("INSERT INTO `investments` (`id`, `name`, `currency`, `amount`, `category`, `isDeleted`, `createdAt`) VALUES ('1', 'SNP 500', 'USD', 500, 'ETF', 0, 1764362076)")
                db.execSQL("INSERT INTO `weights` (`id`, `value`, `isDeleted`, `createdAt`) VALUES ('1', '80.5', 0, 1764362076)")
                db.close()
            }

        // Re-open the database with latest version and provide
        // all migrations as the migration process.
        helper.runMigrationsAndValidate(
            name = TEST_DB,
            version = 18,
            validateDroppedTables = true,
            migrations = arrayOf(
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
                MIGRATION_12_13,
                MIGRATION_13_14,
                MIGRATION_14_15,
                MIGRATION_15_16,
                MIGRATION_16_17,
                MIGRATION_17_18,
            ),
        ).close()

        // verifications
        Room.databaseBuilder(context, AppDatabase::class.java, TEST_DB)
            .allowMainThreadQueries()
            .build()
            .let { db ->
                // verify default values after migration
                runBlocking {
                    val actualTodo = db.todoDao().getById("1")
                    val expectedTodo = TodoEntity(
                        id = "1",
                        content = "Buy groceries\nMilk, eggs, bread, and fruits",
                        isCompleted = false,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedTodo, actualTodo)

                    val actualExpense = db.expenseDao().getById("1")
                    val expectedExpense = ExpenseEntity(
                        id = "1",
                        name = "Groceries",
                        amount = 75.5.toBigDecimal(),
                        currency = "EUR",
                        category = "Food",
                        date = 1764362076,
                        excludeFromAnalytics = false,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedExpense, actualExpense)

                    val actualInvestment = db.investmentDao().getById("1")
                    val expectedInvestment = InvestmentEntity(
                        id = "1",
                        name = "SNP 500",
                        shares = 500.toBigDecimal(),
                        cost = 0.toBigDecimal(),
                        category = "ETF",
                        currency = "USD",
                        date = 1764362076,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedInvestment, actualInvestment)

                    val actualWeight = db.weightDao().getById("1")
                    val expectedWeight = WeightEntity(
                        id = "1",
                        value = 80.5.toBigDecimal(),
                        date = 1764362076,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedWeight, actualWeight)
                }

                // update the new fields
                runBlocking {
                    val todo = TodoEntity(
                        id = "1",
                        content = "Buy groceries updated",
                        isCompleted = false,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    db.todoDao().upsert(todo)

                    val expense = ExpenseEntity(
                        id = "1",
                        name = "Groceries",
                        amount = 75.5.toBigDecimal(),
                        currency = "EUR",
                        category = "Food",
                        date = 1764362076,
                        excludeFromAnalytics = true,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    db.expenseDao().upsert(expense)

                    val investment = InvestmentEntity(
                        id = "1",
                        name = "SNP 500",
                        shares = 500.toBigDecimal(),
                        cost = 250.toBigDecimal(),
                        category = "ETF",
                        currency = "USD",
                        date = 1764438110,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    db.investmentDao().upsert(investment)

                    val weight = WeightEntity(
                        id = "1",
                        value = 81.3.toBigDecimal(),
                        date = 1764438110,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    db.weightDao().upsert(weight)

                    val watchlistAsset = WatchlistAssetEntity(
                        id = "1",
                        name = "AAPL",
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    db.watchlistAssetDao().upsert(watchlistAsset)

                    val fuelConsumption = FuelConsumptionEntity(
                        id = "1",
                        amount = 45.0.toBigDecimal(),
                        price = 85.toBigDecimal(),
                        currency = "EUR",
                        type = "Petrol",
                        carName = "Honda Civic",
                        odometer = 50000,
                        date = 1764362076,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    db.fuelConsumptionDao().upsert(fuelConsumption)

                    val carMaintenance = CarMaintenanceEntity(
                        id = "1",
                        description = "Oil change",
                        partNumber = "OIL-5W30",
                        url = null,
                        carName = "Honda Civic",
                        odometer = 50000,
                        date = 1764362076,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    db.carMaintenanceDao().upsert(carMaintenance)

                    val trading = TradeEntity(
                        id = "1",
                        name = "AAPL",
                        shares = 10.toBigDecimal(),
                        marketPrice = 150.toBigDecimal(),
                        fee = 1.5.toBigDecimal(),
                        currency = "USD",
                        orderType = "Buy",
                        date = 1764362076,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    db.tradingDao().upsert(trading)
                }

                // verify new fields are updated
                runBlocking {
                    val actualTodo = db.todoDao().getById("1")
                    val expectedTodo = TodoEntity(
                        id = "1",
                        content = "Buy groceries updated",
                        isCompleted = false,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedTodo, actualTodo)

                    val actualExpense = db.expenseDao().getById("1")
                    val expectedExpense = ExpenseEntity(
                        id = "1",
                        name = "Groceries",
                        amount = 75.5.toBigDecimal(),
                        currency = "EUR",
                        category = "Food",
                        date = 1764362076,
                        excludeFromAnalytics = true,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedExpense, actualExpense)

                    val actualInvestment = db.investmentDao().getById("1")
                    val expectedInvestment = InvestmentEntity(
                        id = "1",
                        name = "SNP 500",
                        shares = 500.toBigDecimal(),
                        cost = 250.toBigDecimal(),
                        category = "ETF",
                        currency = "USD",
                        date = 1764438110,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedInvestment, actualInvestment)

                    val actualWeight = db.weightDao().getById("1")
                    val expectedWeight = WeightEntity(
                        id = "1",
                        value = 81.3.toBigDecimal(),
                        date = 1764438110,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedWeight, actualWeight)

                    val actualWatchlistAsset = db.watchlistAssetDao().getById("1")
                    val expectedWatchlistAsset = WatchlistAssetEntity(
                        id = "1",
                        name = "AAPL",
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedWatchlistAsset, actualWatchlistAsset)

                    val actualFuelConsumption = db.fuelConsumptionDao().getById("1")
                    val expectedFuelConsumption = FuelConsumptionEntity(
                        id = "1",
                        amount = 45.0.toBigDecimal(),
                        price = 85.toBigDecimal(),
                        currency = "EUR",
                        type = "Petrol",
                        carName = "Honda Civic",
                        odometer = 50000,
                        date = 1764362076,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedFuelConsumption, actualFuelConsumption)

                    val actualCarMaintenance = db.carMaintenanceDao().getById("1")
                    val expectedCarMaintenance = CarMaintenanceEntity(
                        id = "1",
                        description = "Oil change",
                        partNumber = "OIL-5W30",
                        url = null,
                        carName = "Honda Civic",
                        odometer = 50000,
                        date = 1764362076,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedCarMaintenance, actualCarMaintenance)

                    val actualTrading = db.tradingDao().getById("1")
                    val expectedTrading = TradeEntity(
                        id = "1",
                        name = "AAPL",
                        shares = 10.toBigDecimal(),
                        marketPrice = 150.toBigDecimal(),
                        fee = 1.5.toBigDecimal(),
                        currency = "USD",
                        orderType = "Buy",
                        date = 1764362076,
                        isDeleted = false,
                        createdAt = 1764362076,
                    )

                    assertEquals(expectedTrading, actualTrading)
                }

                db.close()
            }
    }

    companion object {

        private const val TEST_DB = "migration-test-db"
    }
}