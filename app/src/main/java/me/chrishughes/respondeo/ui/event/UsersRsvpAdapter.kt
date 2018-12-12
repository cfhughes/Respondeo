package me.chrishughes.respondeo.ui.event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import me.chrishughes.respondeo.AppExecutors
import me.chrishughes.respondeo.R
import me.chrishughes.respondeo.ui.common.DataBoundListAdapter
import me.chrishughes.respondeo.vo.Member
import me.chrishughes.respondeo.databinding.UsersrsvpItemBinding;

class UsersRsvpAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors
) : DataBoundListAdapter<Member, UsersrsvpItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Member>() {
        override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
            return oldItem.memberName == newItem.memberName && oldItem.photoLink == newItem.photoLink
        }

    }
) {
    override fun createBinding(parent: ViewGroup): UsersrsvpItemBinding {
        val binding = DataBindingUtil
            .inflate<UsersrsvpItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.usersrsvp_item,
                parent,
                false,
                dataBindingComponent
            )
            return binding
        }

    override fun bind(binding: UsersrsvpItemBinding, item: Member) {
        binding.rsvp = item;
    }

}