package me.chrishughes.respondeo.ui.common

import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.recyclerview.widget.DiffUtil
import me.chrishughes.respondeo.AppExecutors
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bind(binding: EventItemBinding, item: Event) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}