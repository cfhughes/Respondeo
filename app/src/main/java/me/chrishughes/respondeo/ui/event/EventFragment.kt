package me.chrishughes.respondeo.ui.event

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import me.chrishughes.respondeo.databinding.EventFragmentBinding
import me.chrishughes.respondeo.AppExecutors
import me.chrishughes.respondeo.R
import me.chrishughes.respondeo.binding.FragmentDataBindingComponent
import me.chrishughes.respondeo.di.Injectable
import me.chrishughes.respondeo.ui.common.RetryCallback
import me.chrishughes.respondeo.util.autoCleared
import javax.inject.Inject

class EventFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var eventViewModel: EventViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    // mutable for testing
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<EventFragmentBinding>()

    //TODO Rsvps?
    //private var adapter by autoCleared<ContributorAdapter>()

    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        eventViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(EventViewModel::class.java)
        val params = EventFragmentArgs.fromBundle(arguments)
        eventViewModel.setId(params.id,params.groupName)

        val event = eventViewModel.event
        event?.observe(this, Observer { resource ->
            binding.event = resource.data
            binding.eventResource = resource
        })

        /*val adapter = ContributorAdapter(dataBindingComponent, appExecutors) { contributor ->
            navController().navigate(
                RepoFragmentDirections.showUser(contributor.login)
            )
        }
        this.adapter = adapter
        binding.contributorList.adapter = adapter
        initContributorList(repoViewModel)*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<EventFragmentBinding>(
            inflater,
            R.layout.event_fragment,
            container,
            false
        )
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                eventViewModel.retry()
            }
        }
        binding = dataBinding
        return dataBinding.root
    }

}