package com.example.appsstarttimehandson

import android.widget.EditText

fun EditText.isValidEmail(): Boolean {
    val pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    return this.text.toString().matches(pattern.toRegex())
}

fun EditText.isPhoneNumberValid(): Boolean {
    val number = this.text.toString()

    if (number.isNotEmpty()) {
        val firstDigit = Character.getNumericValue(number[0])
        return when {
            number.length < 10 || firstDigit < 6 -> false
            else -> true
        }
    }

    return false
}

fun EditText.isValueWithinRange(lowRange: Int, highRange: Int): Boolean {
    val text = this.text.toString()
    if (text.isEmpty()) {
        this.error = "Please enter date of birth"
        return false
    }

    return text.toInt() in lowRange..highRange
}
