package com.ivzb.urgat.data.investmentMarketPrice

import com.ivzb.urgat.data.forex.FakeForexRepository
import com.ivzb.urgat.data.marketData.FakeMarketPriceRepository
import com.ivzb.urgat.data.marketPrice.MarketPrice
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class DefaultInvestmentMarketMarketPriceRepositoryTest {

    private lateinit var marketPriceRepository: FakeMarketPriceRepository
    private lateinit var forexRepository: FakeForexRepository
    private lateinit var repository: DefaultInvestmentMarketMarketPriceRepository

    @Before
    fun setup() {
        marketPriceRepository = FakeMarketPriceRepository()
        forexRepository = FakeForexRepository()
        repository = DefaultInvestmentMarketMarketPriceRepository(
            marketPriceRepository = marketPriceRepository,
            forexRepository = forexRepository,
        )
    }

    @Test
    fun `returns null when fetchPrice returns null`() = runTest {
        val result = repository.calculateMarketPrice("UNKNOWN", "USD")

        assertNull(result)
    }

    @Test
    fun `returns null when symbol is in failing symbols`() = runTest {
        marketPriceRepository.failingSymbols.add("FAIL")

        val result = repository.calculateMarketPrice("FAIL", "USD")

        assertNull(result)
    }

    @Test
    fun `returns null when shouldThrow is true`() = runTest {
        marketPriceRepository.shouldThrow = true

        val result = repository.calculateMarketPrice("AAPL", "USD")

        assertNull(result)
    }

    @Test
    fun `returns direct price when currency matches`() = runTest {
        marketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("188.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("AAPL", "USD")

        assertEquals(BigDecimal("190.50"), result)
    }

    @Test
    fun `currency comparison is case insensitive`() = runTest {
        marketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("188.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("AAPL", "usd")

        assertEquals(BigDecimal("190.50"), result)
    }

    @Test
    fun `currency comparison is case insensitive with mixed case`() = runTest {
        marketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "usd",
            previousClosePrice = BigDecimal("188.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("AAPL", "USD")

        assertEquals(BigDecimal("190.50"), result)
    }

    @Test
    fun `normalizes dollar sign currency to USD`() = runTest {
        marketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("188.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("AAPL", "$")

        assertEquals(BigDecimal("190.50"), result)
    }

    @Test
    fun `normalizes euro sign currency to EUR`() = runTest {
        marketPriceRepository.prices["SAP.DE"] = MarketPrice(
            symbol = "SAP.DE",
            description = "SAP SE",
            price = BigDecimal("200.00"),
            currency = "EUR",
            previousClosePrice = BigDecimal("198.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("SAP.DE", "€")

        assertEquals(BigDecimal("200.00"), result)
    }

    @Test
    fun `converts when currencies are different`() = runTest {
        marketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("188.00"),
            pricePrecision = 2,
        )
        forexRepository.rates["USDEUR"] = BigDecimal("0.92")

        val result = repository.calculateMarketPrice("AAPL", "EUR")

        assertEquals(BigDecimal("190.50") * BigDecimal("0.92"), result)
    }

    @Test
    fun `converts from GBP market price to USD currency`() = runTest {
        marketPriceRepository.prices["VUSA.L"] = MarketPrice(
            symbol = "VUSA.L",
            description = "Vanguard S&P 500",
            price = BigDecimal("75.00"),
            currency = "GBP",
            previousClosePrice = BigDecimal("74.50"),
            pricePrecision = 2,
        )
        forexRepository.rates["GBPUSD"] = BigDecimal("1.27")

        val result = repository.calculateMarketPrice("VUSA.L", "USD")

        assertEquals(BigDecimal("75.00") * BigDecimal("1.27"), result)
    }

    @Test
    fun `converts from EUR market price to GBP currency`() = runTest {
        marketPriceRepository.prices["SXR8.DE"] = MarketPrice(
            symbol = "SXR8.DE",
            description = "iShares Core S&P 500",
            price = BigDecimal("500.00"),
            currency = "EUR",
            previousClosePrice = BigDecimal("498.00"),
            pricePrecision = 2,
        )
        forexRepository.rates["EURGBP"] = BigDecimal("0.86")

        val result = repository.calculateMarketPrice("SXR8.DE", "GBP")

        assertEquals(BigDecimal("500.00") * BigDecimal("0.86"), result)
    }

    @Test
    fun `normalizes currency before comparison with BTC-EUR mapping`() = runTest {
        marketPriceRepository.prices["COIN"] = MarketPrice(
            symbol = "COIN",
            description = "Coinbase",
            price = BigDecimal("250.00"),
            currency = "BTC-EUR",
            previousClosePrice = BigDecimal("248.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("COIN", "BTC")

        assertEquals(BigDecimal("250.00"), result)
    }

    @Test
    fun `normalizes currency before comparison with ETH-EUR mapping`() = runTest {
        marketPriceRepository.prices["TOKEN"] = MarketPrice(
            symbol = "TOKEN",
            description = "Token",
            price = BigDecimal("3500.00"),
            currency = "ETH-EUR",
            previousClosePrice = BigDecimal("3480.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("TOKEN", "ETH")

        assertEquals(BigDecimal("3500.00"), result)
    }

    @Test
    fun `non-mapped currency stays as is`() = runTest {
        marketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("188.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("AAPL", "USD")

        assertEquals(BigDecimal("190.50"), result)
    }

    @Test
    fun `fetches price exactly once per call`() = runTest {
        marketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("188.00"),
            pricePrecision = 2,
        )

        val fetchCountBefore = marketPriceRepository.fetchCount

        repository.calculateMarketPrice("AAPL", "USD")

        assertEquals(1, marketPriceRepository.fetchCount - fetchCountBefore)
    }

    @Test
    fun `returns zero price when market price is zero and same currency`() = runTest {
        marketPriceRepository.prices["DELIST"] = MarketPrice(
            symbol = "DELIST",
            description = "Delisted",
            price = BigDecimal.ZERO,
            currency = "USD",
            previousClosePrice = BigDecimal("5.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("DELIST", "USD")

        assertEquals(BigDecimal.ZERO, result)
    }

    @Test
    fun `handles very large price same currency`() = runTest {
        marketPriceRepository.prices["BRK.A"] = MarketPrice(
            symbol = "BRK.A",
            description = "Berkshire Hathaway A",
            price = BigDecimal("650000.00"),
            currency = "USD",
            previousClosePrice = BigDecimal("649500.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("BRK.A", "USD")

        assertEquals(BigDecimal("650000.00"), result)
    }

    @Test
    fun `handles very small price same currency`() = runTest {
        marketPriceRepository.prices["SHIB"] = MarketPrice(
            symbol = "SHIB",
            description = "Shiba Inu",
            price = BigDecimal("0.00000850"),
            currency = "USD",
            previousClosePrice = BigDecimal("0.00000845"),
            pricePrecision = 8,
        )

        val result = repository.calculateMarketPrice("SHIB", "USD")

        assertEquals(BigDecimal("0.00000850"), result)
    }

    @Test
    fun `with shares returns null when fetchPrice returns null`() = runTest {
        val result = repository.calculateMarketPrice("UNKNOWN", "USD", BigDecimal("10"))

        assertNull(result)
    }

    @Test
    fun `with shares returns price multiplied by shares same currency`() = runTest {
        marketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("188.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("AAPL", "USD", BigDecimal("10"))

        assertEquals(BigDecimal("190.50") * BigDecimal("10"), result)
    }

    @Test
    fun `with shares converts and multiplies when currencies differ`() = runTest {
        marketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("188.00"),
            pricePrecision = 2,
        )
        forexRepository.rates["USDEUR"] = BigDecimal("0.92")

        val result = repository.calculateMarketPrice("AAPL", "EUR", BigDecimal("5"))

        val expectedPrice = BigDecimal("190.50") * BigDecimal("0.92")
        val expected = expectedPrice * BigDecimal("5")
        assertEquals(expected, result)
    }

    @Test
    fun `with shares handles zero shares`() = runTest {
        marketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("188.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("AAPL", "USD", BigDecimal.ZERO)

        assertEquals(BigDecimal("190.50") * BigDecimal.ZERO, result)
    }

    @Test
    fun `with shares handles fractional shares`() = runTest {
        marketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("188.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("AAPL", "USD", BigDecimal("0.5"))

        assertEquals(BigDecimal("190.50") * BigDecimal("0.5"), result)
    }

    @Test
    fun `with shares handles very large shares`() = runTest {
        marketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("188.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("AAPL", "USD", BigDecimal("1000000"))

        assertEquals(BigDecimal("190.50") * BigDecimal("1000000"), result)
    }

    @Test
    fun `with shares normalizes dollar sign currency and multiplies`() = runTest {
        marketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("188.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("AAPL", "$", BigDecimal("3"))

        assertEquals(BigDecimal("190.50") * BigDecimal("3"), result)
    }

    @Test
    fun `with shares normalizes euro sign currency and multiplies`() = runTest {
        marketPriceRepository.prices["SAP.DE"] = MarketPrice(
            symbol = "SAP.DE",
            description = "SAP SE",
            price = BigDecimal("200.00"),
            currency = "EUR",
            previousClosePrice = BigDecimal("198.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("SAP.DE", "€", BigDecimal("7"))

        assertEquals(BigDecimal("200.00") * BigDecimal("7"), result)
    }

    @Test
    fun `BRK dot B currency normalization does not affect currency comparison`() = runTest {
        marketPriceRepository.prices["TEST"] = MarketPrice(
            symbol = "TEST",
            description = "Test",
            price = BigDecimal("100.00"),
            currency = "BRK-B",
            previousClosePrice = BigDecimal("99.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("TEST", "BRK.B")

        assertEquals(BigDecimal("100.00"), result)
    }

    @Test
    fun `SXR8 currency normalization maps to SXR8 dot DE`() = runTest {
        marketPriceRepository.prices["TEST"] = MarketPrice(
            symbol = "TEST",
            description = "Test",
            price = BigDecimal("100.00"),
            currency = "SXR8.DE",
            previousClosePrice = BigDecimal("99.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("TEST", "SXR8")

        assertEquals(BigDecimal("100.00"), result)
    }

    @Test
    fun `XAU currency normalization maps to GC=F`() = runTest {
        marketPriceRepository.prices["GOLD"] = MarketPrice(
            symbol = "GOLD",
            description = "Gold",
            price = BigDecimal("2300.00"),
            currency = "GC=F",
            previousClosePrice = BigDecimal("2295.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("GOLD", "XAU")

        assertEquals(BigDecimal("2300.00"), result)
    }

    @Test
    fun `unmapped currency does not trigger conversion when matching`() = runTest {
        marketPriceRepository.prices["TEST"] = MarketPrice(
            symbol = "TEST",
            description = "Test",
            price = BigDecimal("50.00"),
            currency = "CHF",
            previousClosePrice = BigDecimal("49.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("TEST", "CHF")

        assertEquals(BigDecimal("50.00"), result)
    }

    @Test
    fun `unmapped currency triggers conversion when not matching`() = runTest {
        marketPriceRepository.prices["TEST"] = MarketPrice(
            symbol = "TEST",
            description = "Test",
            price = BigDecimal("50.00"),
            currency = "CHF",
            previousClosePrice = BigDecimal("49.00"),
            pricePrecision = 2,
        )
        forexRepository.rates["CHFUSD"] = BigDecimal("1.12")

        val result = repository.calculateMarketPrice("TEST", "USD")

        assertEquals(BigDecimal("50.00") * BigDecimal("1.12"), result)
    }

    @Test
    fun `dollar sign normalization triggers same currency path`() = runTest {
        marketPriceRepository.prices["SPY"] = MarketPrice(
            symbol = "SPY",
            description = "SPDR S&P 500",
            price = BigDecimal("520.00"),
            currency = "USD",
            previousClosePrice = BigDecimal("518.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("SPY", "$")

        assertEquals(BigDecimal("520.00"), result)
    }

    @Test
    fun `euro sign normalization triggers same currency path`() = runTest {
        marketPriceRepository.prices["DAX"] = MarketPrice(
            symbol = "DAX",
            description = "DAX Index",
            price = BigDecimal("18500.00"),
            currency = "EUR",
            previousClosePrice = BigDecimal("18450.00"),
            pricePrecision = 2,
        )

        val result = repository.calculateMarketPrice("DAX", "€")

        assertEquals(BigDecimal("18500.00"), result)
    }

    @Test
    fun `dollar sign normalization triggers conversion when market currency is EUR`() = runTest {
        marketPriceRepository.prices["SAP.DE"] = MarketPrice(
            symbol = "SAP.DE",
            description = "SAP SE",
            price = BigDecimal("200.00"),
            currency = "EUR",
            previousClosePrice = BigDecimal("198.00"),
            pricePrecision = 2,
        )
        forexRepository.rates["EURUSD"] = BigDecimal("1.09")

        val result = repository.calculateMarketPrice("SAP.DE", "$")

        assertEquals(BigDecimal("200.00") * BigDecimal("1.09"), result)
    }

    @Test
    fun `euro sign normalization triggers conversion when market currency is USD`() = runTest {
        marketPriceRepository.prices["AAPL"] = MarketPrice(
            symbol = "AAPL",
            description = "Apple Inc.",
            price = BigDecimal("190.50"),
            currency = "USD",
            previousClosePrice = BigDecimal("188.00"),
            pricePrecision = 2,
        )
        forexRepository.rates["USDEUR"] = BigDecimal("0.92")

        val result = repository.calculateMarketPrice("AAPL", "€")

        assertEquals(BigDecimal("190.50") * BigDecimal("0.92"), result)
    }
}

