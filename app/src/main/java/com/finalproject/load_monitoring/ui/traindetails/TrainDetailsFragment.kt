package com.finalproject.load_monitoring.ui.traindetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finalproject.load_monitoring.R
import com.finalproject.load_monitoring.utils.DateFormatUtils
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TrainDetailsFragment : Fragment() {

    private lateinit var rvCarriages: RecyclerView
    private lateinit var closeButton: AppCompatImageButton
    private lateinit var toolbarTitle: MaterialTextView
    private lateinit var tvOriginStationTittle: MaterialTextView
    private lateinit var tvOriginStationName: MaterialTextView
    private lateinit var tvDestinationStationTittle: MaterialTextView
    private lateinit var tvDestinationStationName: MaterialTextView
    private lateinit var tvPlatformNumber: MaterialTextView
    private lateinit var tvPlatformTittle: MaterialTextView
    private lateinit var tvLastUpdateTittle: MaterialTextView
    private lateinit var tvLastUpdateValue: MaterialTextView
    private lateinit var carriagesAdapter: CarriagesAdapter
    private val viewModel: TrainDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_train_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews(view)
        setupRecyclerView()
        setupCloseButton()
        bindUi()
        val trainId = requireArguments().getString("trainId") ?: return
        viewModel.loadTrainDetails(trainId)
    }

    private fun findViews(view: View) {
        rvCarriages = view.findViewById(R.id.rvCarriages)
        closeButton = view.findViewById(R.id.closeButton)
        toolbarTitle = view.findViewById(R.id.toolbarTitle)
        tvLastUpdateValue = view.findViewById(R.id.tvLastUpdateValue)
        tvLastUpdateTittle = view.findViewById(R.id.tvLastUpdateTittle)
        tvOriginStationTittle = view.findViewById(R.id.tvOriginStationTittle)
        tvOriginStationName = view.findViewById(R.id.tvOriginStationName)
        tvDestinationStationTittle = view.findViewById(R.id.tvDestinationStationTittle)
        tvDestinationStationName = view.findViewById(R.id.tvDestinationStationName)
        tvPlatformNumber = view.findViewById(R.id.tvPlatformNumber)
        tvPlatformTittle = view.findViewById(R.id.tvPlatformTittle)

    }

    private fun setupRecyclerView() {
        rvCarriages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        carriagesAdapter = CarriagesAdapter(emptyList())
        rvCarriages.adapter = carriagesAdapter
    }

    private fun setupCloseButton() {
        closeButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun bindUi() {
        // Here the fragment start listening to viewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Every time the value of '_trainDetails' inside the ViewModel changes
                // (when a call is made to the server), the collect function receives the new value (details)
                // and runs the code inside it.

                viewModel.trainDetails.collect { details ->
                    if (details == null) return@collect

                    // Origin station
                    tvOriginStationName.text =
                        " ${details.originStation}"

                    // Destination station
                    tvDestinationStationName.text =
                        "${details.destinationStation} "

                    // Platform number
                    tvPlatformNumber.text = "${details.originPlatform}"

                    // Last updated
                    tvLastUpdateValue.text = details.carriageList
                        .maxByOrNull { it.lastDataUpdate }
                        ?.lastDataUpdate
                        ?.let { DateFormatUtils.formatStringTime(it) }
                        ?: getString(R.string.not_available)

                    carriagesAdapter = CarriagesAdapter(details.carriageList)
                    rvCarriages.adapter = carriagesAdapter
                }
            }
        }
    }
}
