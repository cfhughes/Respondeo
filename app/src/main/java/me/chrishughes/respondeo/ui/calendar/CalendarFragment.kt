package me.chrishughes.respondeo.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
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
            rsvpCheckCallback = { event: Event, checkBox: CompoundButton ->
                checkBox.isEnabled = false
                if (event.rsvpResponse == "yes" || event.rsvpResponse == "waitlist" || event.rsvpResponse == "yes_pending_payment") {
                    eventService.sendRsvp(
                        authorization = "Bearer " + authInfo.accessToken,
                        urlName = event.groupurl,
                        id = event.id,
                        response = "no"
                    ).enqueue(object : Callback<RsvpResult> {
                        override fun onFailure(call: Call<RsvpResult>, t: Throwable) {
                            Toast.makeText(context, "An error occured", LENGTH_LONG).show()
                            checkBox.isChecked = true
                            checkBox.isEnabled = true;
                        }

                        override fun onResponse(call: Call<RsvpResult>, response: Response<RsvpResult>) {
                            if (response.body()?.response == "no") {
                                event.rsvpResponse = "no"
                            }else{
                                Toast.makeText(context, "An error occured", LENGTH_LONG).show()
                                checkBox.isChecked = true
                            }
                            checkBox.isEnabled = true;
                        }

                    })
                }else{
                    eventService.sendRsvp(
                        authorization = "Bearer " + authInfo.accessToken,
                        urlName = event.groupurl,
                        id = event.id,
                        response = "yes"
                    ).enqueue(object : Callback<RsvpResult> {
                        override fun onFailure(call: Call<RsvpResult>, t: Throwable) {
                            Toast.makeText(context, "An error occured", LENGTH_LONG).show()
                            checkBox.isEnabled = true;
                        }

                        override fun onResponse(call: Call<RsvpResult>, response: Response<RsvpResult>) {
                            if (!response.isSuccessful){
                                Toast.makeText(context, "An error occured "+response.errorBody(), LENGTH_LONG).show()
                                checkBox.isChecked = false;
                            } else if (response.body()?.response == "yes") {
                                event.rsvpResponse = "yes"
                            } else if (response.body()?.response == "waitlist") {
                                event.rsvpResponse = "waitlist"
                                Toast.makeText(context, "You have been waitlisted", LENGTH_LONG).show()
                            } else if (response.body()?.response == "yes_pending_payment") {
                                event.rsvpResponse = "yes_pending_payment"
                                Toast.makeText(context, "You will still need to pay for this event", LENGTH_LONG).show()
                            }else{
                                Toast.makeText(context, "An error occured", LENGTH_LONG).show()
                                checkBox.isChecked = false;
                            }
                            checkBox.isEnabled = true;
                        }

                    })
                }
            }
        ) { event ->
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, event.id)
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event.name)
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "event")
            FirebaseAnalytics.getInstance(activity!!).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
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