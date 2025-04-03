package com.example.silent_scheduler.presentation.ui

import android.app.TimePickerDialog
import android.content.Intent
import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.silent_scheduler.data.local.entity.SilentSchedule
import com.example.silent_scheduler.databinding.FragmentHomeBinding
import com.example.silent_scheduler.presentation.viewmodel.HomeViewModel
import com.google.android.material.chip.Chip
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModel()

    private var startTimeInMillis = 0L
    private var endTimeInMillis = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe LiveData from ViewModel
        observeViewModel()

        // Initialize UI interactions
        setupUI()

        // Request necessary permissions
        checkAndRequestPermissions()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.schedule.collect { schedule ->
                schedule?.let {
                    binding.etStartTime.setText(
                        String.format(
                            "%02d:%02d",
                            it.startHour,
                            it.startMinute
                        )
                    )
                    binding.etEndTime.setText(String.format("%02d:%02d", it.endHour, it.endMinute))
                    restoreSelectedDays(it.days)
                    startTimeInMillis = getTimeInMillis(it.startHour, it.startMinute, true)
                    endTimeInMillis = getTimeInMillis(it.endHour, it.endMinute, false)
                    binding.switchSilent.isChecked = it.isEnabled
                }
            }
        }
    }

    private fun setupUI() {
        binding.apply {
            etStartTime.setOnClickListener { showTimePicker(etStartTime, true) }
            etEndTime.setOnClickListener { showTimePicker(etEndTime, false) }

            btnEnableOverride.setOnClickListener {
                val selectedDays = getSelectedDays()
                if (selectedDays.isEmpty()) {
                    showToast("Select at least one day")
                    return@setOnClickListener
                }
                if (startTimeInMillis > System.currentTimeMillis() && endTimeInMillis > startTimeInMillis) {
                    val schedule = SilentSchedule(
                        startHour = binding.etStartTime.text.toString().split(":")[0].toInt(),
                        startMinute = binding.etStartTime.text.toString().split(":")[1].toInt(),
                        endHour = binding.etEndTime.text.toString().split(":")[0].toInt(),
                        endMinute = binding.etEndTime.text.toString().split(":")[1].toInt(),
                        days = selectedDays.joinToString(","),
                        isEnabled = binding.switchSilent.isChecked
                    )
                    viewModel.saveSchedule(
                        schedule,
                        startTimeInMillis,
                        endTimeInMillis,
                        selectedDays
                    )
                    showToast("Schedule saved successfully")
                } else {
                    showToast("Select valid start and end times")
                }
            }

            switchSilent.setOnCheckedChangeListener { _, isChecked ->
                viewModel.toggleSchedule(isChecked)
            }
        }
    }

    private fun showTimePicker(textView: TextView, isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hour, minute ->
                val formattedTime = String.format("%02d:%02d", hour, minute)
                textView.text = formattedTime
                val timeInMillis = getTimeInMillis(hour, minute, isStartTime)
                if (isStartTime) startTimeInMillis = timeInMillis else endTimeInMillis =
                    timeInMillis
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun getTimeInMillis(hour: Int, minute: Int, isStartTime: Boolean): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis < System.currentTimeMillis()) add(Calendar.DAY_OF_MONTH, 1)
        }
        var timeInMillis = calendar.timeInMillis
        if (!isStartTime && timeInMillis < startTimeInMillis) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            timeInMillis = calendar.timeInMillis
        }
        return timeInMillis
    }

    private fun restoreSelectedDays(days: String) {
        val daysList = days.split(",")
        for (i in 0 until binding.chipGroupsilent.childCount) {
            val chip = binding.chipGroupsilent.getChildAt(i) as? Chip
            chip?.isChecked = daysList.contains(chip?.text.toString())
        }
    }

    private fun getSelectedDays(): Set<String> {
        val selectedDays = mutableSetOf<String>()
        for (i in 0 until binding.chipGroupsilent.childCount) {
            val chip = binding.chipGroupsilent.getChildAt(i) as? Chip
            if (chip?.isChecked == true) {
                chip.text?.toString()?.let { selectedDays.add(it) }
            }
        }
        return selectedDays
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAndRequestPermissions() {
        val requiredPermissions = listOf(
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.ACCESS_NOTIFICATION_POLICY
        )

        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }

        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (!notificationManager.isNotificationPolicyAccessGranted) {
            requestDndAccess()
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun requestDndAccess() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            showToast("Permissions granted.")
        } else {
            showToast("Please grant all permissions for silent mode scheduling.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
    }
}
