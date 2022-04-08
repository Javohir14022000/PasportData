package com.example.pasportdata

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.airbnb.lottie.BuildConfig
import com.example.pasportdata.adapters.RvAdapter
import com.example.pasportdata.database.AppDatabase
import com.example.pasportdata.databinding.CustomDialogBinding
import com.example.pasportdata.databinding.FragmentAddPersonBinding
import com.example.pasportdata.databinding.ItemBottomsheetBinding
import com.example.pasportdata.entity.Person
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class AddPersonFragment : Fragment(R.layout.fragment_add_person) {
    private val binding: FragmentAddPersonBinding by viewBinding()
    private lateinit var appDatabase: AppDatabase
    private var currentPhotoPath: String = ""
    lateinit var list: ArrayList<Person>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())
        binding.apply {

            imgPerson.setOnClickListener {
                getPermission.launch(
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
                alertDialog()
            }
            t7.setOnClickListener {
                val dialog = DatePickerDialog(requireContext(),
                    { view, year, month, dayOfMonth -> date.text = "$dayOfMonth/$month/$year" },
                    2022,
                    12,
                    12
                )

                dialog.show()
            }
            t8.setOnClickListener {
                val dialog =
                    DatePickerDialog(requireContext(), object : DatePickerDialog.OnDateSetListener {
                        override fun onDateSet(
                            view: DatePicker?,
                            year: Int,
                            month: Int,
                            dayOfMonth: Int
                        ) {
                            dateEnd.text = "$dayOfMonth/$month/$year"
                        }

                    }, 2032, 12, 12)

                dialog.show()
            }
            card1Save.setOnClickListener {
                if (etName.text.isNotEmpty() && etSureName.text.isNotEmpty() && etFatherName.text.isNotEmpty() && etCity.text.isNotEmpty() && etAddress.text.isNotEmpty() && date.text.isNotEmpty() && dateEnd.text.isNotEmpty() && currentPhotoPath.isNotEmpty()) {
                    val name = etName.text.toString()
                    val surname = etSureName.text.toString()
                    val middleName = etFatherName.text.toString()
                    val spRegionText = spRegion.selectedItem.toString()
                    val city = etCity.text.toString()
                    val address = etAddress.text.toString()
                    val passportData = date.text.toString()
                    val passportEndData = dateEnd.text.toString()
                    val spGenderText = spGender.selectedItem.toString()
                    val image = currentPhotoPath
                    val numbers = generatePassportNumber()

                    val person = Person(
                        name = name,
                        surname = surname,
                        middle_name = middleName, region = spRegionText,
                        city = city,
                        address = address,
                        passport_date = passportData,
                        passport_end_date = passportEndData,
                        gender = spGenderText,
                        image = image,
                        numberPassport = numbers

                    )
                    val bottomSheetDialog =
                        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogThem)
                    val itemBottomSheetDialog =
                        ItemBottomsheetBinding.inflate(
                            LayoutInflater.from(requireContext()),
                            null,
                            false
                        )
                    itemBottomSheetDialog.yes.setOnClickListener {
                        appDatabase.personDao().addPerson(person)
                        bottomSheetDialog.dismiss()
                        findNavController().popBackStack()
                    }
                    itemBottomSheetDialog.no.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.setContentView(itemBottomSheetDialog.root)
                    bottomSheetDialog.show()

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Ma'lumotlar to`liq kiritilmagan",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            exitIv.setOnClickListener {
                findNavController().popBackStack()
            }

        }
    }

    private fun alertDialog() {
        val dialog = AlertDialog.Builder(requireContext())
        val customDialogBinding = CustomDialogBinding.inflate(layoutInflater)
        val create = dialog.create() as AlertDialog
        create.setView(customDialogBinding.root)
        create.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        customDialogBinding.apply {
            galereya.setOnClickListener {
                getGalleryContent.launch("image/*")
                create.hide()
            }
            camera.setOnClickListener {
                getCameraNewMethod()
                create.hide()
            }
        }
        create.show()
    }

    private fun getCameraNewMethod() {
        val photoFile = try {
            createImageFile()
        } catch (e: Exception) {
            null
        }
        photoFile?.also {
            val photoUri =
                FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID, it)
            getTakePhoto.launch(photoUri)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val m = System.currentTimeMillis()
        val externalFilesDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile(m.toString(), ".jpg", externalFilesDir)
        currentPhotoPath = file.absolutePath
        Log.d("Path", "Path : $currentPhotoPath")

        return file
    }

    private val getTakePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            binding.imgPerson.setImageURI(Uri.fromFile(File(currentPhotoPath)))
        }
    }

    private val getPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

        }
    private val getGalleryContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it == null) return@registerForActivityResult
            binding.imgPerson.setImageURI(it)
            val m = System.currentTimeMillis()
            val openInputStream = activity?.contentResolver?.openInputStream(it)
            val file = File(activity?.filesDir, "$m.jpg")
            val fileOutputStream = FileOutputStream(file)
            openInputStream?.copyTo(fileOutputStream)
            openInputStream?.close()
            fileOutputStream.close()
            val absolutePath = file.absolutePath
            currentPhotoPath = file.absolutePath
            Log.d("Path", "Path: $absolutePath")
        }

    private fun generatePassportNumber(): String {
        val number = (100000..9999999).random()
        val firstLetter = (65..65).random().toChar()
        val secondLetter = (65..68).random().toChar()
        var notExists = true
        val passportNumber = "$firstLetter$secondLetter $number"
        if (this::list.isInitialized) {
            for (i in 0 until list.size) {
                if (list.get(i).equals(passportNumber)) {
                    notExists = false
                    break
                }
            }

        } else list = ArrayList()
        if (notExists) {
            return passportNumber
        }
        return generatePassportNumber()
    }

}