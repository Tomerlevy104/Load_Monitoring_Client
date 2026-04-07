package com.finalproject.load_monitoring.ui.trainslist

import com.finalproject.load_monitoring.ui.traindetails.TrainDetailsFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finalproject.load_monitoring.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.getValue


class TrainsListFragment : Fragment() {
    private lateinit var tvOrigin: MaterialTextView
    private lateinit var tvDestination: MaterialTextView
    private lateinit var rvTrains: RecyclerView
    private lateinit var trainCardAdapter: TrainCardAdapter
    private lateinit var btnSwap: MaterialButton
    private lateinit var btnBack: ImageButton

    private val viewModel: TrainsListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trains_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews(view)
        setupRecyclerView()
        bindUi()
        setupOriginAndDestinationFromSearch()
        setupListeners()
//        viewModel.loadAllTrains() // TODO: Remove this line when we have a search endpoint

//        viewModel.loadTrainsListByOriginAndDestination(
//            Bundle.getString("origin"),
//            Bundle.getString("destination")
//        )
    }

    private fun setupListeners() {
        btnSwap.setOnClickListener {
            val currentOrigin = tvOrigin.text.toString()
            val currentDestination = tvDestination.text.toString()
            val dateTime = arguments?.getString("dateTime") ?: ""
            tvOrigin.text = currentDestination
            tvDestination.text = currentOrigin
            viewModel.loadTrainsListByOriginDestAndDate(currentDestination, currentOrigin, LocalDateTime.parse(dateTime))
        }

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupOriginAndDestinationFromSearch() {
        val origin = arguments?.getString("origin") ?: ""
        val destination = arguments?.getString("destination") ?: ""
        val dateTime = arguments?.getString("dateTime") ?: ""
        tvOrigin.text = origin
        tvDestination.text = destination
        viewModel.loadTrainsListByOriginDestAndDate(origin, destination, LocalDateTime.parse(dateTime))
    }

    private fun findViews(view: View) {
        tvOrigin = view.findViewById(R.id.tvOrigin)
        tvDestination = view.findViewById(R.id.tvDestination)
        rvTrains = view.findViewById(R.id.rvTrains)
        btnSwap = view.findViewById(R.id.btnSwap)
        btnBack = view.findViewById(R.id.btnBack)
    }

    private fun setupRecyclerView() {
        rvTrains.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        trainCardAdapter = TrainCardAdapter(emptyList()) { train ->
            // Navigate to TrainDetailsFragment with the train ID as an argument
            val bundle = Bundle().apply {
                putString("trainId", train.trainID)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TrainDetailsFragment::class.java, bundle)
                .addToBackStack(null)
                .commit()
        }
        rvTrains.adapter = trainCardAdapter
    }

    private fun bindUi() {
        // Here the fragment start listening to viewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Every time the value of '_trainsList' inside the ViewModel changes
                // the collect function receives the new value (details)
                // and runs the code inside it.

                viewModel.trainsList.collect { list ->
                    trainCardAdapter.submitList(list)
                }
            }
        }
    }


}