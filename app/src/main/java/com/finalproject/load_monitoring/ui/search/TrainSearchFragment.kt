package com.finalproject.load_monitoring.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.finalproject.load_monitoring.R
import com.finalproject.load_monitoring.ui.trainslist.TrainsListFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import android.widget.ArrayAdapter
import android.util.Log

class TrainSearchFragment : Fragment() {

    private lateinit var closeButton: AppCompatImageButton

    private lateinit var tilOrigin: TextInputLayout
    private lateinit var etOrigin: MaterialAutoCompleteTextView
    private lateinit var tilDestination: TextInputLayout
    private lateinit var etDestination: MaterialAutoCompleteTextView
    private lateinit var npHour: NumberPicker
    private lateinit var npMinute: NumberPicker

    private lateinit var btnSwap: MaterialButton
    private lateinit var btnSearch: MaterialButton

    private lateinit var originAdapter: ArrayAdapter<String>
    private lateinit var destinationAdapter: ArrayAdapter<String>

    private val viewModel: TrainSearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_train_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViews(view)
        setupNumberPickers()
        setupAutoComplete()
        setupListeners()
        bindUi()
    }

    private fun findViews(view: View) {
        closeButton = view.findViewById(R.id.closeButton)

        tilOrigin = view.findViewById(R.id.tilOrigin)
        etOrigin = view.findViewById(R.id.etOrigin)

        tilDestination = view.findViewById(R.id.tilDestination)
        etDestination = view.findViewById(R.id.etDestination)

        npHour = view.findViewById(R.id.npHour)
        npMinute = view.findViewById(R.id.npMinute)

        btnSwap = view.findViewById(R.id.btnSwap)
        btnSearch = view.findViewById(R.id.btnSearch)
    }

    private fun setupNumberPickers() {
        // Hour: 0..23
        npHour.minValue = 0
        npHour.maxValue = 23
        npHour.wrapSelectorWheel = true

        // Minute: 0..59
        npMinute.minValue = 0
        npMinute.maxValue = 59
        npMinute.wrapSelectorWheel = true

        // If we want 2 digits on the wheel (08 instead of 8)
        npHour.displayedValues = Array(24) { i -> "%02d".format(i) }
        npMinute.displayedValues = Array(60) { i -> "%02d".format(i) }
    }

    private fun setupListeners() {
        closeButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Typing origin station
        etOrigin.addTextChangedListener { text ->
            tilOrigin.error = null
            viewModel.onOriginChanged(text?.toString().orEmpty())
        }

        // Typing destination station
        etDestination.addTextChangedListener { text ->
            tilDestination.error = null
            viewModel.onDestinationChanged(text?.toString().orEmpty())
        }

        npHour.setOnValueChangedListener { _, _, newVal ->
            viewModel.onHourChanged(newVal)
        }

        npMinute.setOnValueChangedListener { _, _, newVal ->
            viewModel.onMinuteChanged(newVal)
        }

        // Swap button
        btnSwap.setOnClickListener {
            viewModel.onSwapStations()
        }

        btnSearch.setOnClickListener {
            val origin = etOrigin.text?.toString().orEmpty().trim()
            val destination = etDestination.text?.toString().orEmpty().trim()
            // Hour
            val hour = npHour.value
            val minute = npMinute.value

            val bundle = Bundle().apply {
                putString("origin", origin)
                putString("destination", destination)
                putInt("hour", hour)
                putInt("minute", minute)
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TrainsListFragment::class.java, bundle)
                .addToBackStack(null)
                .commit()
        }

        etOrigin.setOnItemClickListener { parent, _, position, _ ->
            val selectedStation = parent.getItemAtPosition(position).toString()
            viewModel.onOriginChanged(selectedStation)
        }

        etDestination.setOnItemClickListener { parent, _, position, _ ->
            val selectedStation = parent.getItemAtPosition(position).toString()
            viewModel.onDestinationChanged(selectedStation)
        }
    }

    private fun bindUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: TrainSearchUiState) {
        val stationNames = state.stations.map { it.stationName }

        originAdapter.clear()
        originAdapter.addAll(stationNames)
        originAdapter.notifyDataSetChanged()

        destinationAdapter.clear()
        destinationAdapter.addAll(stationNames)
        destinationAdapter.notifyDataSetChanged()
        val originNow = etOrigin.text?.toString().orEmpty()
        if (originNow != state.origin) {
            etOrigin.setText(state.origin, false)
            etOrigin.setSelection(state.origin.length)
        }

        val destNow = etDestination.text?.toString().orEmpty()
        if (destNow != state.destination) {
            etDestination.setText(state.destination, false)
            etDestination.setSelection(state.destination.length)
        }

        // NumberPickers
        if (npHour.value != state.hour) npHour.value = state.hour
        if (npMinute.value != state.minute) npMinute.value = state.minute

        // Search button enable/disable
        btnSearch.isEnabled = state.isSearchEnabled
        btnSearch.alpha = if (state.isSearchEnabled) 1f else 0.6f
    }

    private fun setupAutoComplete() {
        originAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        )

        destinationAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        )

        etOrigin.setAdapter(originAdapter)
        etDestination.setAdapter(destinationAdapter)

        etOrigin.threshold = 1
        etDestination.threshold = 1
    }
}
