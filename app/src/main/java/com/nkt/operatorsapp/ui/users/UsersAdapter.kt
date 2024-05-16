package com.nkt.operatorsapp.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nkt.operatorsapp.data.User
import com.nkt.operatorsapp.databinding.ExistingUserBinding

class UsersAdapter(
    private val deleteUser: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UsersVH>() {

    var users = emptyList<User>()

    inner class UsersVH(
        private val binding: ExistingUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            with(binding) {
                usernameText.text = user.username
                userTypeText.text = user.type.toString()
                deleteUserButton.setOnClickListener {
                    deleteUser(user)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersVH {
        val binding = ExistingUserBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return UsersVH(binding)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UsersVH, position: Int) {
        holder.bind(user = users[position])
    }
}