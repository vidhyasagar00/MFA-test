package test.myPackage.mfa.routee.biometric

import test.myPackage.mfa.routee.biometric.BiometricFingerPrintStatus

interface BiometricResultCallback {
    /**
     * It will returns status of finger print permission
     *
     * @param granted A boolean value with with status of permission
     */
    fun permissionGranted(biometricFingerPrintStatus: BiometricFingerPrintStatus)
}