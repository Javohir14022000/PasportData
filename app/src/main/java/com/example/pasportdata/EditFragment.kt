package com.example.pasportdata

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.pasportdata.database.AppDatabase
import com.example.pasportdata.databinding.CustomDialogBinding
import com.example.pasportdata.databinding.FragmentEditBinding
import com.example.pasportdata.databinding.ItemBottomsheetBinding
import com.example.pasportdata.entity.Person
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception


class EditFragment : Fragment(R.layout.fragment_edit) {
    private val binding: FragmentEditBinding by viewBinding()
    private lateinit var appDatabase: AppDatabase
    private var currentPhotoPath: String = ""
    private lateinit var list: ArrayList<Person>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabase = AppDatabase.getInstance(this.requireContext())

        val id = arguments?.getInt("id", 0)

        val person = appDatabase.personDao().getPersonById(id ?: 1)


        binding.apply {
            etName.setText(person.name)
            etSureName.setText(person.surname)
            etFatherName.setText(person.middle_name)
            etCity.setText(person.city)
            etAddress.setText(person.address)
            date.setText(person.passport_date)
            dateEnd.setText(person.passport_end_date)
            imgPerson.setImageURI(Uri.fromFile(File(person.image)))

            imgPerson.setOnClickListener {
                alertDialog()
            }
            t7.setOnClickListener {
                val dialog =
                    DatePickerDialog(requireContext(), object : DatePickerDialog.OnDateSetListener {
                        override fun onDateSet(
                            view: DatePicker?,
                            year: Int,
                            month: Int,
                            dayOfMonth: Int
                        ) {
                            date.text = "$dayOfMonth/$month/$year"
                        }

                    }, 2022, 1, 1)

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

                    }, 2032, 1, 1)

                dialog.show()
            }

            exitIv.setOnClickListener {
                findNavController().popBackStack()
            }

            card1Save.setOnClickListener {

                if (etName.text.isNotEmpty() && etSureName.text.isNotEmpty() && etFatherName.text.isNotEmpty() && etCity.text.isNotEmpty() && etAddress.text.isNotEmpty() && date.text.isNotEmpty() && dateEnd.text.isNotEmpty()) {
                    person.name = etName.text.toString()
                    person.surname = etSureName.text.toString()
                    person.middle_name = etFatherName.text.toString()
                    person.region = spRegion.selectedItem.toString()
                    person.city = etCity.text.toString()
                    person.address = etAddress.text.toString()
                    person.passport_date = date.text.toString()
                    person.passport_end_date = dateEnd.text.toString()
                    person.gender = spGender.selectedItem.toString()
                    person.image = currentPhotoPath
                    person.numberPassport = generatePassportNumber()
                    val bottomSheetDialog =
                        BottomSheetDialog(requireContext(), R.style.BottomSheetDialogThem)
                    val itemBottomSheetDialog =
                        ItemBottomsheetBinding.inflate(
                            LayoutInflater.from(requireContext()),
                            null,
                            false
                        )
                    itemBottomSheetDialog.yes.setOnClickListener {
                        appDatabase.personDao().editPerson(person)
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
                        "Ma'lumotlar to'liq kiritilmadi",
                        Toast.LENGTH_SHORT
                    ).show()
                }

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
        val passportNumber = firstLetter.toString() + secondLetter.toString() + number.toString()
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