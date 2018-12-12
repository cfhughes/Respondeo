package me.chrishughes.respondeo.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import me.chrishughes.respondeo.AppExecutors
import me.chrishughes.respondeo.R
import me.chrishughes.respondeo.api.AuthInfo
import me.chrishughes.respondeo.api.EventService
import me.chrishughes.respondeo.binding.FragmentDataBindingComponent
import me.chrishughes.respondeo.databinding.CalendarFragmentBinding
import me.chrishughes.respondeo.di.Injectable
import me.chrishughes.respondeo.ui.common.EventListAdapter
import me.chrishughes.respondeo.util.autoCleared
import me.chrishughes.respondeo.vo.Event
import me.chrishughes.respondeo.vo.RsvpResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class CalendarFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var eventService: EventService

    @Inject
    lateinit var authInfo: AuthInfo

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    var binding by autoCleared<CalendarFragmentBinding>()

    var adapter by autoCleared<EventListAdapter>()

    lateinit var calendarViewModel: CalendarViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        initRecyclerView()
        val rvAdapter = EventListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors,
            rsvpCheckCallback = {event: Event ->
                event.rsvpEnabled = false;
                if (event.rsvpResponse == "yes"){
                    eventService.sendRsvp(
                        authorization = authInfo.accessToken,
                        urlName = event.groupurl,
                        id = event.id,
                        response = "no"
                    ).enqueue(object : Callback<RsvpResult> {
                        override fun onFailure(call: Call<RsvpResult>, t: Throwable) {
                            Toast.makeText(context, "An error occured", LENGTH_LONG).show()
                            event.rsvpResponse = "yes"
                        }

                        override fun onResponse(call: Call<RsvpResult>, response: Response<RsvpResult>) {
                            event.rsvpEnabled = true;
                            if (response.body()!!.response == "no"){
                                event.rsvpResponse = "no"
                            }else{
                                Toast.makeText(context, "An error occured", LENGTH_LONG).show()
                                event.rsvpResponse = "yes"
                            }
                        }

                    })
                }else{
                    eventService.sendRsvp(
                        authorization = authInfo.accessToken,
                        urlName = event.groupurl,
                        id = event.id,
                        response = "yes"
                    ).enqueue(object : Callback<RsvpResult> {
                        override fun onFailure(call: Call<RsvpResult>, t: Throwable) {
                            Toast.makeText(context, "An error occured", LENGTH_LONG).show()
                            event.rsvpResponse = "no"
                        }

                        override fun onResponse(call: Call<RsvpResult>, response: Response<RsvpResult>) {
                            event.rsvpEnabled = true;
                            if (!response.isSuccessful){
                                Toast.makeText(context, "An error occured "+response.errorBody(), LENGTH_LONG).show()
                                event.rsvpResponse = "no"
                            }else if (response.body()!!.response == "yes"){
                                event.rsvpResponse = "yes"
                            }else{
                                Toast.makeText(context, "An error occured", LENGTH_LONG).show()
                                event.rsvpResponse = "no"
                            }
                        }

                    })
                }
            }
        ) { event ->
            findNavController(this).navigate(
                CalendarFragmentDirections.showEvent(event.id, event.groupurl)
            )
        }
        binding.eventList.adapter = rvAdapter
        adapter = rvAdapter
    }

    private fun initRecyclerView() {
        calendarViewModel.results.observe(this, Observer { result ->
            binding.eventsResource = result
            adapter.submitList(result.data)
        })
    }

}