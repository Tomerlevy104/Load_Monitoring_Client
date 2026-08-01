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
import java.time.ZonedDateTime
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

        tvCameraCount.text = "טוען..."
        tvIRCount.text = "טוען..."
        tvNumber.text = "קרון מס׳ ${carriage.carriageNumber}"
        tvCurrentOcc.text = carriage.occupancy.toString()
        tvMaxOcc.text = " / ${carriage.maxCapacity} נוסעים"

        val targetFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        val formattedDate = carriage.lastDataUpdate?.let { rawDateStr ->
            try {
                // 1. קודם כל מנקים רווחים מיותרים אם יש
                val cleanStr = rawDateStr.trim()

                // 2. משתמשים ב-LocalDateTime שמסתדר מצוין עם פורמט T ובלי אזור זמן
                // אם הסטרינג מכיל מילישניות, הוא ידע להתעלם מהן לבד בזמן ה-formatting
                val parsedDate = LocalDateTime.parse(cleanStr.substringBefore("Z"))

                parsedDate.format(targetFormatter)
            } catch (e: Exception) {
                // הדפס את השגיאה ל-Logcat כדי שתוכל לראות בדיוק מה הגיע מהשרת
                android.util.Log.e("DateTimeError", "Failed to parse: $rawDateStr", e)

                // פתרון גיבוי מהיר: אם הכל נכשל, פשוט נקה את הסטרינג ידנית במקום להציג "לא זמין"
                rawDateStr.replace("T", " ").substringBefore(".")
            }
        } ?: getString(R.string.not_available)

        tvLastUpdate.text = "זמן עדכון אחרון: $formattedDate"

        // Calculate occupancy percentage for the progress bar (safeguard against division by zero)
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
                    tvCameraCount.text = log.cameraCount.toString()
                    tvIRCount.text = log.irCount.toString()
                } else {
                    tvCameraCount.text = "לא זמין"
                    tvIRCount.text = "לא זמין"
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
                        "${details.originStation}"

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

                    carriagesAdapter = CarriagesAdapter(details.carriageList) { clickedCarriage ->
                        showCarriageDetailsDialog(clickedCarriage)
                    }
                    rvCarriages.adapter = carriagesAdapter
                }
            }
        }
    }
}
