package test.myPackage.mfa.routee.interfaces

import test.myPackage.mfa.routee.retrofit.APIResult

interface APICallback {
    fun apiResult(result: APIResult<*>)
}