package me.chrishughes.respondeo.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import me.chrishughes.respondeo.AppExecutors
import me.chrishughes.respondeo.R
import me.chrishughes.respondeo.binding.FragmentDataBindingComponent
import me.chrishughes.respondeo.di.Injectable
import me.chrishughes.respondeo.ui.common.EventListAdapter
import me.chrishughes.respondeo.util.autoCleared
import javax.inject.Inject

class CalendarFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<CalendarFragmentBinding>()

    var adapter by autoCleared<EventListAdapter>()

    lateinit var calendarViewModel: CalendarViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.calendar_fragment,
            container,
            false,
            dataBindingComponent
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        calendarViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(CalendarViewModel::class.java)
        //initRecyclerView()
        val rvAdapter = EventListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors
        ) { event ->


        }
    }








}