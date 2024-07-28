package com.example.database

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private lateinit var viewModel: FriendViewModel

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

        val data = Friend(name, school, hobby)
        lifecycleScope.launch {
            viewModel.insertFriend(data)
        }
        finish()
    }
}
