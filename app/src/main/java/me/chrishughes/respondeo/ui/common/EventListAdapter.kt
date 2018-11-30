package me.chrishughes.respondeo.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import me.chrishughes.respondeo.AppExecutors
import me.chrishughes.respondeo.R
import me.chrishughes.respondeo.databinding.EventItemBinding
import me.chrishughes.respondeo.vo.Event

class EventListAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val eventClickCallback: ((Event) -> Unit)?
) : DataBoundListAdapter<Event, EventItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.name == newItem.name
        }
    }
) {
    override fun createBinding(parent: ViewGroup): EventItemBinding {
        val binding = DataBindingUtil.inflate<EventItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.event_item,
            parent,
            false,
            dataBindingComponent
        )
        binding.root.setOnClickListener {
            binding.event?.let {
                eventClickCallback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: EventItemBinding, item: Event) {
        binding.event = item
    }

}