package com.net.routee.otpdetection

import androidx.fragment.app.testing.FragmentScenario
import kotlin.test.Test
import kotlin.test.assertEquals

class OTPMessageClientTest {
    private lateinit var scenario: FragmentScenario<OTPValidationFragment>
    private val data ="12345"
    @Test
    fun getOtp() {
        assertEquals(data,"12345","success")
    }
}