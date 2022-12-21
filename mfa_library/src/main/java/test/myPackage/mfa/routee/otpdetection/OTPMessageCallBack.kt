package test.myPackage.mfa.routee.otpdetection

interface OTPMessageCallBack {
    /**
     * It is a call back function to set and get the otpMessage
     *
     * @param otpMessage The otp number which used to set and get
     */
    fun getOTP(otpMessage: String)
}