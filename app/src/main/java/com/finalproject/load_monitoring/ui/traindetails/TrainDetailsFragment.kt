package com.finalproject.load_monitoring.ui.traindetails

import android.os.Bundle
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
        // 1. טעינת העיצוב המותאם אישית של הדיאלוג
        val dialogView = layoutInflater.inflate(R.layout.dialog_carriage_details, null)

        // 2. יצירת הדיאלוג
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        // הפיכת הרקע המרובע הדיפולטיבי לשקוף כדי שנראה את הפינות המעוגלות שלנו
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 3. קישור הרכיבים בדיאלוג
        val btnBack = dialogView.findViewById<ImageButton>(R.id.btnBackDialog)
        val tvId = dialogView.findViewById<TextView>(R.id.tvDialogCarriageId)
        val tvNumber = dialogView.findViewById<TextView>(R.id.tvDialogCarriageNumber)
        val tvCurrentOcc = dialogView.findViewById<TextView>(R.id.tvDialogOccupancyCurrent)
        val tvMaxOcc = dialogView.findViewById<TextView>(R.id.tvDialogOccupancyMax)
        val progressOcc = dialogView.findViewById<LinearProgressIndicator>(R.id.progressOccupancy)
        val tvLastUpdate = dialogView.findViewById<TextView>(R.id.tvDialogLastUpdate)

        // 4. השמת הנתונים מה-Model לתוך הדיאלוג
        tvId.text = "מזהה ייחודי: ${carriage.carriageID}"
        tvNumber.text = "קרון מס׳ ${carriage.carriageNumber}"
        tvCurrentOcc.text = carriage.occupancy.toString()
        tvMaxOcc.text = " / ${carriage.maxCapacity} נוסעים"
        tvLastUpdate.text = "זמן עדכון אחרון: ${carriage.lastDataUpdate}"

        // חישוב אחוז התפוסה בשביל סרגל ההתקדמות (מניעת חלוקה באפס ליתר ביטחון)
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

        // כפתור חזרה סוגר את הדיאלוג
        btnBack.setOnClickListener {
            dialog.dismiss()
        }

        // 5. הצגת הדיאלוג למשתמש
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
