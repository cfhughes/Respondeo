package me.chrishughes.respondeo.ui.event

import androidx.databinding.DataBindingComponent
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.chrishughes.respondeo.R
import me.chrishughes.respondeo.binding.FragmentBindingAdapters
import me.chrishughes.respondeo.testing.SingleFragmentActivity
import me.chrishughes.respondeo.util.CountingAppExecutorsRule
import me.chrishughes.respondeo.util.DataBindingIdlingResourceRule
import me.chrishughes.respondeo.util.EspressoTestUtil
import me.chrishughes.respondeo.util.RecyclerViewMatcher
import me.chrishughes.respondeo.util.TaskExecutorWithIdlingResourceRule
import me.chrishughes.respondeo.util.TestUtil
import me.chrishughes.respondeo.util.ViewModelUtil
import me.chrishughes.respondeo.vo.Event
import me.chrishughes.respondeo.vo.Member
import me.chrishughes.respondeo.vo.Resource
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class EventFragmentTest {
    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)
    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()
    @Rule
    @JvmField
    val countingAppExecutors = CountingAppExecutorsRule()
    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    private val repoLiveData = MutableLiveData<Resource<Event>>()
    private val rsvpsLiveData = MutableLiveData<Resource<List<Member>>>()
    private lateinit var viewModel: EventViewModel
    private lateinit var mockBindingAdapter: FragmentBindingAdapters


    private val eventFragment = EventFragment().apply {
        arguments = EventFragmentArgs("a", "b").toBundle()
    }

    @Before
    fun init() {
        viewModel = mock(EventViewModel::class.java)
        mockBindingAdapter = mock(FragmentBindingAdapters::class.java)
        doNothing().`when`(viewModel).setId(anyString(), anyString())
        `when`(viewModel.event).thenReturn(repoLiveData)
        `when`(viewModel.rsvps).thenReturn(rsvpsLiveData)
        eventFragment.appExecutors = countingAppExecutors.appExecutors
        eventFragment.viewModelFactory = ViewModelUtil.createFor(viewModel)
        eventFragment.dataBindingComponent = object : DataBindingComponent {
            override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
                return mockBindingAdapter
            }
        }
        activityRule.activity.setFragment(eventFragment)
        EspressoTestUtil.disableProgressBarAnimations(activityRule)
    }

    @Test
    fun testRsvps() {
        setRsvps(2)
        onView(listMatcher().atPosition(0))
            .check(matches(hasDescendant(withText("Bob 500"))))
        onView(listMatcher().atPosition(1))
            .check(matches(hasDescendant(withText("Bob 501"))))
    }

    private fun listMatcher(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.rsvps_view)
    }

    private fun setRsvps(count: Int) {
        val rsvps = List<Member>(count) {
            TestUtil.createMember(it+500L)
        }
        rsvpsLiveData.postValue(Resource.success(rsvps))
    }

}

