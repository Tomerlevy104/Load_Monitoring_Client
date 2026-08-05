package com.finalproject.load_monitoring.ui.traindetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.finalproject.load_monitoring.R
import com.finalproject.load_monitoring.models.CarriageModel
import com.finalproject.load_monitoring.models.OccupancyLevel
import com.finalproject.load_monitoring.utils.DateFormatUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TrainDetailsFragment : Fragment() {

    private lateinit var rvCarriages: RecyclerView
    private lateinit var closeButton: AppCompatImageButton
    private lateinit var tvOriginStationName: MaterialTextView
    private lateinit var tvDestinationStationName: MaterialTextView
    private lateinit var tvPlatformNumber: MaterialTextView
    private lateinit var tvLastUpdateValue: MaterialTextView
    private lateinit var carriagesAdapter: CarriagesAdapter

    private lateinit var swipeRefresh: SwipeRefreshLayout
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
        swipeRefresh.setOnRefreshListener {
            viewModel.loadTrainDetails(trainId)
            swipeRefresh.isRefreshing = false
        }
    }

    private fun findViews(view: View) {
        rvCarriages = view.findViewById(R.id.rvCarriages)
        closeButton = view.findViewById(R.id.closeButton)
        tvLastUpdateValue = view.findViewById(R.id.tvLastUpdateValue)
        tvOriginStationName = view.findViewById(R.id.tvOriginStationName)
        tvDestinationStationName = view.findViewById(R.id.tvDestinationStationName)
        tvPlatformNumber = view.findViewById(R.id.tvPlatformNumber)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
    }

    private fun setupRecyclerView() {
        rvCarriages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        carriagesAdapter = CarriagesAdapter(emptyList()) { clickedCarriage ->
            showCarriageDetailsDialog(clickedCarriage)
        }
        rvCarriages.adapter = carriagesAdapter
    }

    private fun showCarriageDetailsDialog(carriage: CarriageModel, colorRes: Int? = null) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_carriage_details, null)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        viewModel.clearOccupancyLog()
        viewModel.getOccupancyLogByCarriageId(carriage.carriageID.toLong())

        val btnBack = dialogView.findViewById<ImageButton>(R.id.btnBackDialog)
        val tvNumber = dialogView.findViewById<TextView>(R.id.tvDialogCarriageNumber)
        val tvCurrentOcc = dialogView.findViewById<TextView>(R.id.tvDialogOccupancyCurrent)
        val tvCameraCount = dialogView.findViewById<TextView>(R.id.tvCameraCount)
        val tvIRCount = dialogView.findViewById<TextView>(R.id.tvIRCount)
        val tvMaxOcc = dialogView.findViewById<TextView>(R.id.tvDialogOccupancyMax)
        val progressOcc = dialogView.findViewById<LinearProgressIndicator>(R.id.progressOccupancy)
        val tvLastUpdate = dialogView.findViewById<TextView>(R.id.tvDialogLastUpdate)

        tvCameraCount.text = getString(R.string.loading)
        tvIRCount.text = getString(R.string.loading)
        tvNumber.text = "קרון מס׳ ${carriage.carriageNumber}"
        tvCurrentOcc.text = carriage.occupancy.toString()
        tvMaxOcc.text = " / ${carriage.maxCapacity} נוסעים"

        val targetFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        // Server may send timestamps with a trailing "Z" and/or milliseconds;
        // strip those and fall back to manual cleanup if parsing still fails.
        val formattedDate = carriage.lastDataUpdate?.let { rawDateStr ->
            try {
                val cleanStr = rawDateStr.trim()
                val parsedDate = LocalDateTime.parse(cleanStr.substringBefore("Z"))
                parsedDate.format(targetFormatter)
            } catch (e: Exception) {
                android.util.Log.e("DateTimeError", "Failed to parse: $rawDateStr", e)
                rawDateStr.replace("T", " ").substringBefore(".")
            }
        } ?: getString(R.string.not_available)

        tvLastUpdate.text = "זמן עדכון אחרון: $formattedDate"

        // Safeguard against division by zero
        val occupancyPercentage = if (carriage.maxCapacity > 0) {
            ((carriage.occupancy.toFloat() / carriage.maxCapacity.toFloat()) * 100).toInt()
        } else {
            0
        }
        progressOcc.progress = occupancyPercentage
        val indicatorColorRes = when (carriage.occupancyStatus) {
            OccupancyLevel.LOW -> R.color.green
            OccupancyLevel.MEDIUM -> R.color.yellow
            OccupancyLevel.HIGH -> R.color.red
            else -> R.color.gray
        }
        progressOcc.setIndicatorColor(requireContext().getColor(indicatorColorRes))

        val dialogJob = viewLifecycleOwner.lifecycleScope.launch {
            viewModel.occupancyLog.collect { log ->
                if (log != null) {
                    // Show "not available" instead of a raw 0 when a sensor is down -
                    // a stale/placeholder 0 must never be mistaken for a real reading.
                    tvCameraCount.text = if (log.cameraStatus == "unavailable") {
                        getString(R.string.not_available)
                    } else {
                        log.cameraCount.toString()
                    }
                    tvIRCount.text = if (log.irStatus == "unavailable") {
                        getString(R.string.not_available)
                    } else {
                        log.irCount.toString()
                    }
                } else {
                    tvCameraCount.text = getString(R.string.not_available)
                    tvIRCount.text = getString(R.string.not_available)
                }
            }
        }

        btnBack.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            dialogJob.cancel()
        }

        dialog.show()
    }

    private fun setupCloseButton() {
        closeButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun bindUi() {
        // Reactively updates the UI whenever train details are loaded from the ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trainDetails.collect { details ->
                    if (details == null) return@collect

                    tvOriginStationName.text = "${details.originStation}"
                    tvDestinationStationName.text = "${details.destinationStation} "
                    tvPlatformNumber.text = "${details.originPlatform}"

                    tvLastUpdateValue.text = details.carriageList
                        .maxByOrNull { it.lastDataUpdate }
                        ?.lastDataUpdate
                        ?.let { DateFormatUtils.formatStringTime(it) }
                        ?: getString(R.string.not_available)

                    carriagesAdapter = CarriagesAdapter(details.carriageList) { clickedCarriage ->
                        showCarriageDetailsDialog(clickedCarriage)
                    }
                    rvCarriages.adapter = carriagesAdapter
                }
            }
        }
    }
}