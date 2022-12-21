package test.myPackage.mfa.routee.interfaces

import retrofit2.Response

interface AuthCallback {
    fun response(success: Boolean)
}