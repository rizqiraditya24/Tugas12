package com.example.database

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.database.data.Friend
import com.example.database.data.FriendVMFactory
import com.example.database.data.FriendViewModel
import com.example.database.databinding.ActivityAddBinding
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import android.util.Base64

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private lateinit var viewModel: FriendViewModel
    private lateinit var photoFile: File
    private var photoStr: String = ""

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val parcelFileDescriptor = contentResolver.openFileDescriptor(
                    result?.data?.data
                        ?: return@registerForActivityResult, "r"
                )
                val fileDescriptor = parcelFileDescriptor?.fileDescriptor
                val inputStream = FileInputStream(fileDescriptor)
                val outputStream = FileOutputStream(photoFile)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                parcelFileDescriptor?.close()

                val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
                binding.ivPhoto.setImageBitmap(takenImage)
                photoStr = bitmapToString(takenImage)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add)
        val viewModelFactory = FriendVMFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[FriendViewModel::class.java]

        binding.viewModel = viewModel
        binding.activity = this
        binding.lifecycleOwner = this

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.ivPhoto.setOnClickListener {
            galleryAction()
        }
    }

    private fun galleryAction() {
        val readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_GALLERY_PERMISSION_CODE)
        } else {
            initializePhotoFile()
            openGallery()
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PHOTO", ".jpg", storageDir)
    }

    private fun initializePhotoFile() {
        photoFile = try {
            createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(this, "Cannot create image file", Toast.LENGTH_SHORT).show()
            throw ex
        }
    }

    fun bitmapToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun stringToBitmap(encodedString: String): Bitmap?{
        return try {
            val byteArray = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }catch (e: IllegalArgumentException){
            e.printStackTrace()
            null
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_GALLERY_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializePhotoFile()
                    openGallery()
                } else {
                    Toast.makeText(this, "Permission denied to access gallery", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_GALLERY_PERMISSION_CODE = 1
    }

    fun onSaveClick() {
        addData()
    }

    private fun addData() {
        val name = viewModel.name.value?.trim()
        val school = viewModel.school.value?.trim()
        val hobby = viewModel.hobby.value?.trim()

        if (name.isNullOrEmpty() || school.isNullOrEmpty() || hobby.isNullOrEmpty()) {
            Toast.makeText(this, "Isi bagian yang kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val data = Friend(name, school, hobby, photoStr)
        lifecycleScope.launch {
            viewModel.insertFriend(data)
        }
        finish()
    }
}
