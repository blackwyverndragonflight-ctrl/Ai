package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.PersianUtils
import com.example.viewmodel.WalletViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("سامانه پزیرندگان", appName)
    }

    @Test
    fun `test initial state is empty on fresh install`() {
        val vm = WalletViewModel()
        assertTrue(vm.uiState.value.customers.isEmpty())
        assertTrue(vm.uiState.value.stores.isEmpty())
        assertTrue(vm.uiState.value.transactions.isEmpty())
    }

    @Test
    fun `test live Persian date calculation`() {
        val persianDate = PersianUtils.getCurrentPersianDate()
        assertTrue(persianDate.isNotEmpty())
        assertTrue(persianDate.startsWith("۱۴"))
        assertTrue(persianDate.contains("/"))
    }

    @Test
    fun `test national ID validation`() {
        // Valid national ID (checksum: sum of 001234567 is 112, 112%11=2, check digit is 11-2=9)
        assertTrue(PersianUtils.isValidNationalId("0012345679"))
        // Invalid lengths or repetitive digits
        assertFalse(PersianUtils.isValidNationalId("123"))
        assertFalse(PersianUtils.isValidNationalId("1111111111"))
    }

    @Test
    fun `test Persian digits formatting`() {
        val formatted = PersianUtils.toPersianDigits("12345")
        assertEquals("۱۲۳۴۵", formatted)
        val toman = PersianUtils.formatToman(5000000L)
        assertTrue(toman.contains("تومان"))
    }

    @Test
    fun `test wallet discharge logic`() {
        val vm = WalletViewModel()
        vm.seedInitialData()
        val customer = vm.uiState.value.customers.first { !it.isDischarged }
        val targetStore = vm.uiState.value.stores.first()

        vm.confirmDischargeAndTransfer(
            customerId = customer.id,
            storeId = targetStore.id,
            employerPercentage = 5.0,
            storePercentage = 3.0,
            dischargeDate = "۱۴۰۳/۰۸/۱۵"
        )

        val updatedCustomer = vm.uiState.value.customers.first { it.id == customer.id }
        assertTrue(updatedCustomer.isDischarged)
    }

    @Test
    fun `test json restore functionality`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val vm = WalletViewModel()
        val testJson = """
            {
              "customers": [
                {
                  "id": "test-c1",
                  "nationalId": "0012345678",
                  "firstName": "علی",
                  "lastName": "رضایی",
                  "phoneNumber": "09120000000",
                  "shebaNumber": "IR123456789012345678901234",
                  "walletBalance": 10000000
                }
              ],
              "stores": [
                {
                  "id": "test-s1",
                  "name": "فروشگاه آزمایشی",
                  "address": "تهران",
                  "location": "مرکزی"
                }
              ]
            }
        """.trimIndent()

        val restored = vm.restoreFromJsonContent(context, testJson)
        assertTrue(restored)
        assertEquals(1, vm.uiState.value.customers.size)
        assertEquals(1, vm.uiState.value.stores.size)
        assertEquals("علی", vm.uiState.value.customers[0].firstName)
        assertEquals("فروشگاه آزمایشی", vm.uiState.value.stores[0].name)
    }
}
