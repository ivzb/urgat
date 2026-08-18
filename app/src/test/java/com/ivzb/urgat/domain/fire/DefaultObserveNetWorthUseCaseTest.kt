package com.ivzb.urgat.domain.fire

import com.ivzb.urgat.data.fire.FakeFireRepository
import com.ivzb.urgat.data.fire.FiRe
import com.ivzb.urgat.data.fire.NetWorth
import com.ivzb.urgat.data.fire.StashBreakdownItem
import com.ivzb.urgat.util.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultObserveNetWorthUseCaseTest {

    private lateinit var fakeObserveFiReUseCase: FakeObserveFiReUseCase
    private lateinit var fakeFireRepository: FakeFireRepository

    private lateinit var observeUseCase: ObserveNetWorthUseCase

    @Before
    fun setUp() {
        fakeObserveFiReUseCase = FakeObserveFiReUseCase()
        fakeFireRepository = FakeFireRepository()

        observeUseCase = DefaultObserveNetWorthUseCase(
            fakeObserveFiReUseCase,
            fakeFireRepository,
        )
    }

    @Test
    fun `invoke should map accumulated stash to net worth`() = runTest {
        fakeObserveFiReUseCase.emit(Async.Success(FIRE))

        val actual = observeUseCase().first()

        assertEquals(
            Async.Success(
                NetWorth(
                    amount = "125000".toBigDecimal(),
                    currency = "EUR",
                    breakdown = BREAKDOWN,
                )
            ),
            actual,
        )
    }

    @Test
    fun `invoke should return null when there is not enough fire data`() = runTest {
        fakeObserveFiReUseCase.emit(Async.Success(null))

        val actual = observeUseCase().first()

        assertEquals(Async.Success(null), actual)
    }

    @Test
    fun `invoke should return Error when fire data fails`() = runTest {
        fakeObserveFiReUseCase.emit(Async.Error)

        val actual = observeUseCase().first()

        assertEquals(Async.Error, actual)
    }

    @Test
    fun `invoke should return Loading while fire data is loading`() = runTest {
        val actual = observeUseCase().first()

        assertEquals(Async.Loading, actual)
    }

    companion object {

        private val BREAKDOWN = listOf(
            StashBreakdownItem("Stocks", "100000".toBigDecimal()),
            StashBreakdownItem("Savings", "25000".toBigDecimal()),
        )

        private val FIRE = FiRe(
            selection = "December 2025",
            currency = "EUR",

            monthlyIncome = "5000".toBigDecimal(),
            monthlyExpenses = "3000".toBigDecimal(),
            monthlyInvestments = "2000".toBigDecimal(),

            annualIncome = "60000".toBigDecimal(),
            annualExpenses = "36000".toBigDecimal(),
            annualInvestments = "24000".toBigDecimal(),

            accumulatedStash = "125000".toBigDecimal(),
            stashBreakdown = BREAKDOWN,
            savingsRate = "40.00".toBigDecimal(),
            target = "990000.00".toBigDecimal(),

            withdrawalRate = "4".toBigDecimal(),
            returnOfInvestment = "7".toBigDecimal(),
            taxRate = "10".toBigDecimal(),

            yearsUntilFiRe = "17.74".toBigDecimal(),
        )

    }

}

