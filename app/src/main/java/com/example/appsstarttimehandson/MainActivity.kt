package com.example.appsstarttimehandson

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
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
    private lateinit var course: Spinner
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()

        submitButton = findViewById<Button?>(R.id.buttonSubmit).apply {
            setOnClickListener {
                if (isFormDataValid()) {
                    Snackbar.make(this, "Form Submitted successfully", Snackbar.LENGTH_LONG).show()
                    val intent = Intent(this@MainActivity, RestActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        reportFullyDrawn()
    }

    private fun setupViews() {
        imageView = findViewById<ImageView?>(R.id.image_view).apply {
            val an = AnimationUtils.loadAnimation(this@MainActivity, R.anim.placeholder_animation)
            this.startAnimation(an)

            lifecycleScope.launch {
                val bitmap = getImageAfterEdit()

                clearAnimation()
                setImageBitmap(bitmap)
            }
        }
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
        course = findViewById(R.id.course_selection)
    }

    private suspend fun getImageAfterEdit(): Bitmap {
        // Heavy operation - image processing
        return withContext(Dispatchers.IO) {
            val initialBitmap = BitmapFactory.decodeResource(resources, R.drawable.college)
            val filteredBitmap = applyFilter(initialBitmap)
            applyGaussianBlur(filteredBitmap, 10F)
        }
    }

    private suspend fun applyFilter(bitmap: Bitmap): Bitmap = coroutineScope {
        // Apply a grayscale filter to the image
        val matrix = ColorMatrix().apply {
            setSaturation(0f)
        }
        val filter = ColorMatrixColorFilter(matrix)

        val paint = Paint().apply {
            colorFilter = filter
        }

        val editedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(editedBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        editedBitmap
    }

    private suspend fun applyGaussianBlur(imageBitmap: Bitmap, radius: Float): Bitmap = coroutineScope {
        // Create a RenderScript context
        val rsContext = RenderScript.create(applicationContext)

        // Create an input allocation from the image bitmap
        val inputAllocation = Allocation.createFromBitmap(rsContext, imageBitmap)

        // Create an output allocation for the blurred image
        val outputAllocation = Allocation.createTyped(rsContext, inputAllocation.type)

        // Create a script for Gaussian blur
        val blurScript = ScriptIntrinsicBlur.create(rsContext, Element.U8_4(rsContext))

        // Set the blur radius
        blurScript.setRadius(radius)

        // Perform the Gaussian blur operation
        blurScript.setInput(inputAllocation)
        blurScript.forEach(outputAllocation)

        // Copy the blurred image from the output allocation to a new bitmap
        val blurredBitmap = Bitmap.createBitmap(imageBitmap.width, imageBitmap.height, imageBitmap.config)
        outputAllocation.copyTo(blurredBitmap)

        // Destroy the RenderScript context and resources
        inputAllocation.destroy()
        outputAllocation.destroy()
        blurScript.destroy()
        rsContext.destroy()

        // Return the blurred image
        blurredBitmap
    }

    private fun isFormDataValid(): Boolean {
        return when {
            name.text.toString().isEmpty() -> {
                name.error = "Enter name"
                return false
            }

            name.text.length < 4 -> {
                name.error = "Name should be more than 4 character"
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

            !dobDay.isValueWithinRange(1, 31)-> {
                dobDay.error = "Invalid date of birth."
                return false
            }

            !dobMonth.isValueWithinRange(1, 12) -> {
                dobMonth.error = "Invalid date of month value."
                return false
            }

            !dobYear.isValueWithinRange(1923, 2023) -> {
                dobYear.error = "Invalid date of year value."
                return false
            }

            landmark.text.toString().isEmpty() -> {
                landmark.error = "Please enter valid landmark"
                return false
            }

            district.text.toString().isEmpty() -> {
                district.error = "Please enter valid district"
                return false
            }

            zipcode.text.toString().isEmpty() -> {
                zipcode.error = "Please enter zip code"
                return false
            }

            zipcode.text.toString().length < 6 -> {
                zipcode.error = "Please enter valid zip code"
                return false
            }

            qualification.text.toString().isEmpty() -> {
                qualification.error = "Please enter qualification"
                return false
            }

            school.text.toString().isEmpty() -> {
                school.error = "Please enter your school"
                return false
            }

            grade.selectedItem.toString().equals("Select grade") -> {
                Snackbar.make(grade, "Please select grade", Snackbar.LENGTH_LONG).show()
                return false
            }

            course.selectedItem.toString().equals("Select course") -> {
                Snackbar.make(course, "Please select course", Snackbar.LENGTH_LONG).show()
                return false
            }

            else -> true
        }
    }
}
