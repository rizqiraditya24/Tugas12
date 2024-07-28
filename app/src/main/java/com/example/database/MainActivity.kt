package com.example.database

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.database.data.Friend
import com.example.database.data.FriendVMFactory
import com.example.database.data.FriendViewModel
import com.example.database.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var friendList = ArrayList<Friend>()
    private lateinit var viewModel: FriendViewModel
    private lateinit var adapter: AdapterRV

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewModelFactory = FriendVMFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[FriendViewModel::class.java]

        setupRecyclerView()

        observeFriendList()

        binding.btnAdd.setOnClickListener {
            val destination = Intent(this, AddActivity::class.java)
            startActivity(destination)
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteAll()
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            switchMode(isChecked)
        }
    }

    private fun setupRecyclerView() {
        adapter = AdapterRV(this) { friend, isEditModeEnabled, name, hobby, school ->
            if (!isEditModeEnabled) {
                lifecycleScope.launch {
                    viewModel.updateFriend(friend.copy(name = name, hobby = hobby, school = school))
                }
            }
        }
        binding.rvFriend.adapter = adapter
    }

    private fun observeFriendList() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getFriend().collect { friends ->
                    Log.d("DATABASE", "Friends $friends")
                    friendList.clear()
                    friendList.addAll(friends)
                    adapter.setData(friendList)
                }
            }
        }
    }

    private fun switchMode(isChecked: Boolean) {
        if (isChecked) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}