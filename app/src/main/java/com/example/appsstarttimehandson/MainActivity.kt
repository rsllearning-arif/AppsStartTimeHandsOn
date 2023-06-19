package com.example.appsstarttimehandson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var dobDay: EditText
    private lateinit var dobMonth: EditText
    private lateinit var dobYear: EditText
    private lateinit var landmark: EditText
    private lateinit var district: EditText
    private lateinit var zipcode: EditText
    private lateinit var qualification: EditText
    private lateinit var school: EditText
    private lateinit var grade: Spinner
    private lateinit var courseSelected: Spinner
    private lateinit var submitButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()

        submitButton = findViewById<Button?>(R.id.buttonSubmit).apply {
            setOnClickListener {
                if (isFormDataValid()) {
                    Snackbar.make(this, "Form Submitted successfully", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupViews() {
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        phoneNumber = findViewById(R.id.phone_number)
        dobDay = findViewById(R.id.dob_date)
        dobMonth = findViewById(R.id.dob_month)
        dobYear = findViewById(R.id.dob_year)
        landmark = findViewById(R.id.landmark)
        district = findViewById(R.id.district)
        zipcode = findViewById(R.id.zip_code)
        qualification = findViewById(R.id.qualification)
        school = findViewById(R.id.school)
        grade = findViewById(R.id.grades)
        courseSelected = findViewById(R.id.course_selection)
    }

    private fun isFormDataValid(): Boolean {
        return when {
            name.text.toString().isEmpty() -> {
                name.error = "Enter name"
                return false
            }

            !email.isValidEmail() -> {
                email.error = "Email id not valid"
                return false
            }

            !phoneNumber.isPhoneNumberValid() -> {
                phoneNumber.error = "Phone number is not valid"
                return false
            }


            else -> true
        }
    }
}
