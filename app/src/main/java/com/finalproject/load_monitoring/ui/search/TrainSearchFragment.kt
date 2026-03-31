package com.finalproject.load_monitoring.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.finalproject.load_monitoring.R
import com.finalproject.load_monitoring.ui.trainslist.TrainsListFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.launch
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog

class TrainSearchFragment : Fragment() {

    private lateinit var etOrigin: MaterialAutoCompleteTextView
    private lateinit var etDestination: MaterialAutoCompleteTextView
    private lateinit var time: TextView
    private lateinit var timeContainer: LinearLayout

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
        setupAutoComplete()
        setupListeners()
        bindUi()
    }

    private fun findViews(view: View) {

        etOrigin = view.findViewById(R.id.etOrigin)

        etDestination = view.findViewById(R.id.etDestination)

        time = view.findViewById(R.id.tvSelectedTime)
        timeContainer = view.findViewById(R.id.timePickerContainer)

        btnSwap = view.findViewById(R.id.btnSwap)
        btnSearch = view.findViewById(R.id.btnSearch)
    }

    private fun setupListeners() {

        // Typing origin station
        etOrigin.addTextChangedListener { text ->
            viewModel.onOriginChanged(text?.toString().orEmpty())
        }

        // Typing destination station
        etDestination.addTextChangedListener { text ->
            viewModel.onDestinationChanged(text?.toString().orEmpty())
        }

        timeContainer.setOnClickListener {
            showCustomTimePicker()
        }

        // Swap button
        btnSwap.setOnClickListener {
            viewModel.onSwapStations()
        }

        btnSearch.setOnClickListener {
            val origin = etOrigin.text?.toString().orEmpty().trim()
            val destination = etDestination.text?.toString().orEmpty().trim()
            // Hour
            val hour = time.text.split(":")[0].toIntOrNull() ?: 0
            val minute = time.text.split(":")[1].toIntOrNull() ?: 0

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
        Log.d("TrainSearchFragment", "Updated origin stations: $stationNames")
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

        // Search button enable/disable
        btnSearch.isEnabled = state.isSearchEnabled
        btnSearch.alpha = if (state.isSearchEnabled) 1f else 0.6f
    }

    private fun showCustomTimePicker() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.layout_time_picker_sheet, view as? ViewGroup, false)

        val hourPicker = view.findViewById<NumberPicker>(R.id.hourPicker)
        val minutePicker = view.findViewById<NumberPicker>(R.id.minutePicker)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirmTime)
        val tvSelectedTime = requireView().findViewById<TextView>(R.id.tvSelectedTime)

        // הגדרת טווח שעות (0-23)
        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        hourPicker.value = tvSelectedTime.text.split(":")[0].toIntOrNull() ?: 0 // הגדרת שעה ברירת מחדל
        hourPicker.setFormatter { i -> String.format("%02d", i) }

        // הגדרת טווח דקות (0-59)
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.value = tvSelectedTime.text.split(":")[1].toIntOrNull() ?: 0 // הגדרת דקות ברירת מחדל
        minutePicker.setFormatter { i -> String.format("%02d", i) }

        btnConfirm.setOnClickListener {
            val time = String.format("%02d:%02d", hourPicker.value, minutePicker.value)
            tvSelectedTime.text = time
            viewModel.onHourChanged(hourPicker.value)
            viewModel.onMinuteChanged(minutePicker.value)
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
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

        etOrigin.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // פתיחת הרשימה מיד במיקוד השדה לשיפור ה-UX
                etOrigin.showDropDown()
            }
        }

        // פתיחה גם בלחיצה אם השדה כבר בפוקוס
        etOrigin.setOnClickListener {
            etOrigin.showDropDown()
        }
    }
}
