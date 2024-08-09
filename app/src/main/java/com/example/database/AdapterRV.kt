package com.example.database

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.database.data.Friend

class AdapterRV(
    private val context: Context,
    private val onUpdateClick: (Friend, Boolean, String, String, String) -> Unit
) : RecyclerView.Adapter<AdapterRV.ViewHolder>() {

    private var friendList = mutableListOf<Friend>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val etName: EditText = itemView.findViewById(R.id.et_name)
        private val etHobby: EditText = itemView.findViewById(R.id.et_hobby)
        private val etSchool: EditText = itemView.findViewById(R.id.et_school)
        private val btnSave: Button = itemView.findViewById(R.id.btn_save)
        private val ivDisplay: ImageView = itemView.findViewById(R.id.iv_display)

        private var isEditModeEnabled = false

        init {
            btnSave.setOnClickListener {
                isEditModeEnabled = !isEditModeEnabled
                updateEditMode()
                if (!isEditModeEnabled) {
                    val updatedFriend = friendList[adapterPosition].copy(
                        name = etName.text.toString(),
                        hobby = etHobby.text.toString(),
                        school = etSchool.text.toString()
                    )
                    friendList[adapterPosition] = updatedFriend
                    onUpdateClick(
                        updatedFriend,
                        isEditModeEnabled,
                        etName.text.toString(),
                        etHobby.text.toString(),
                        etSchool.text.toString()
                    )
                    notifyItemChanged(adapterPosition)
                }
            }
        }

        fun bind(friend: Friend, isEditModeEnabled: Boolean) {
            this.isEditModeEnabled = isEditModeEnabled
            etName.isEnabled = isEditModeEnabled
            etHobby.isEnabled = isEditModeEnabled
            etSchool.isEnabled = isEditModeEnabled

            etName.setText(friend.name)
            etHobby.setText(friend.hobby)
            etSchool.setText(friend.school)

            val photoBtm = AddActivity().stringToBitmap(friend.photo)
            photoBtm?.let {
                ivDisplay.setImageBitmap(photoBtm)
            }

            updateEditMode()
        }

        private fun updateEditMode() {
            if (isEditModeEnabled) {
                btnSave.text = context.getString(R.string.save)
                etName.isEnabled = true
                etHobby.isEnabled = true
                etSchool.isEnabled = true
            } else {
                btnSave.text = context.getString(R.string.edit)
                etName.isEnabled = false
                etHobby.isEnabled = false
                etSchool.isEnabled = false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFriend = friendList[position]
        holder.bind(currentFriend, false)
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<Friend>) {
        friendList = newList.toMutableList()
        notifyDataSetChanged()
    }
}