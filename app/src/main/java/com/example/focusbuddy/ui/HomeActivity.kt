package com.example.focusbuddy.ui

import android.accessibilityservice.AccessibilityService
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.focusbuddy.R
import com.example.focusbuddy.data.AppBlockerAccessibilityService
import com.example.focusbuddy.data.AppUsageInfo
import com.example.focusbuddy.databinding.ActivityHomeBinding
import com.example.focusbuddy.ui.ui.AppsAdapter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var pieChart: PieChart
    private var updateJob: Job? = null
    private lateinit var totalSosmedUsageText: TextView
    private lateinit var user: TextView
    private lateinit var rv_apps: RecyclerView

    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        totalSosmedUsageText = binding.totalUsageTime
        pieChart = binding.pieChart
        user = binding.user
        rv_apps = binding.rvApps

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("quickSetup").document(uid)
        ref.get().addOnSuccessListener {
            if (it != null){
                val name = it.getString("nama").toString()
                user.text = "Hai, $name"
            }
        }

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check permissions
        if (!hasUsageStatsPermission()) {
            AlertDialog.Builder(this)
                .setTitle("Aktifkan Usage Stats")
                .setMessage("Untuk melihat penggunaan aplikasi, kamu harus mengaktifkan Usage Stats Permission terlebih dahulu.")
                .setCancelable(false)
                .setPositiveButton("Buka Pengaturan") { _, _ ->
                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
                .setNegativeButton("Keluar") { _, _ ->
                    finish()
                }
                .show()
        } else if (!isAccessibilityServiceEnabled(this, AppBlockerAccessibilityService::class.java)) {
            AlertDialog.Builder(this)
                .setTitle("Aktifkan Aksesibilitas")
                .setMessage("Untuk memblokir aplikasi sosial media, kamu harus mengaktifkan Accessibility Service terlebih dahulu.")
                .setCancelable(false)
                .setPositiveButton("Buka Pengaturan") { _, _ ->
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
                .setNegativeButton("Lanjutkan Tanpa Blocker") { _, _ ->
                    // Tetap menampilkan data tanpa blocking
                    setupRecyclerView()
                    checkResetDailyUsage()
                }
                .show()
        } else {
            // Jika semua permission diberikan
            setupRecyclerView()
            checkResetDailyUsage()
        }
    }

    private fun setupRecyclerView() {
        try {
            val appUsageList = loadAppUsageData()
            Log.d("RecyclerView", "Data loaded: ${appUsageList.size} items")

            // Debug output untuk semua aplikasi yang terdeteksi
            appUsageList.forEach { app ->
                Log.d("AppUsage", "App: ${app.appName}, Package: ${app.packageName}, Time: ${app.usageTime/60000} minutes")
            }

            if (appUsageList.isEmpty()) {
                Log.d("RecyclerView", "WARNING: No app usage data available")
                // Tambahkan pesan ke UI jika tidak ada data
                binding.emptyStateText.visibility = View.VISIBLE
                binding.emptyStateText.text = "Tidak ada data penggunaan aplikasi yang tersedia"
            } else {
                binding.emptyStateText?.visibility = View.GONE
            }

            val maxUsage = appUsageList.maxOfOrNull { it.usageTime } ?: 1L

            val adapter = AppsAdapter(appUsageList, maxUsage)

            rv_apps.layoutManager = LinearLayoutManager(this)
            rv_apps.adapter = adapter
            rv_apps.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

            rv_apps.visibility = View.VISIBLE

        } catch (e: Exception) {
            Log.e("RecyclerView", "Error setting up RecyclerView", e)
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasUsageStatsPermission()) {
            updateJob = lifecycleScope.launch {
                while (true) {
                    setupRecyclerView()
                    delay(5000)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        updateJob?.cancel()
    }

    private fun hasUsageStatsPermission(): Boolean {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 1000 * 60 * 60 // 1 jam terakhir
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, beginTime, endTime
        )
        return stats != null && stats.isNotEmpty()
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun startUsageService() {
        val serviceIntent = Intent(this, com.example.focusbuddy.data.UsageStatsService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<out AccessibilityService>): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as android.view.accessibility.AccessibilityManager
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val expectedComponent = "${context.packageName}/${serviceClass.canonicalName}"
        return enabledServices.split(":").contains(expectedComponent)
    }

    private fun checkResetDailyUsage(){
        val prefs = getSharedPreferences("usage_data", Context.MODE_PRIVATE)
        val lastResetTime = prefs.getString("last_reset_time", null)
        val currentTime = android.text.format.DateFormat.format("yyyyMMdd", java.util.Date()).toString()

        if (lastResetTime == null || lastResetTime != currentTime){
            prefs.edit()
                .putInt("sosmed_minutes", 0)
                .putString("last_reset_time", currentTime)
                .apply()
        }
    }

    private fun loadAppUsageData(): List<AppUsageInfo> {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)

        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = calendar.timeInMillis
        Log.d("UsageStats", "BeginTime: ${Date(beginTime)}")
        Log.d("UsageStats", "EndTime: ${Date(endTime)}")

        val rawStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, beginTime, endTime
        )

        Log.d("UsageStats", "Total packages detected: ${rawStats.size}")
        rawStats.forEach { stats ->
            if (stats.totalTimeInForeground > 0) {
                Log.d("UsageStats", "Package: ${stats.packageName}, Time: ${stats.totalTimeInForeground/60000} minutes")
            }
        }

        val allowedApps = mapOf(
            "com.instagram.android" to "Instagram",
            "com.facebook.katana" to "Facebook",
            "com.twitter.android" to "X",
            "com.zhiliaoapp.musically" to "TikTok",
            "com.tiktok.android" to "TikTok",
            "com.ss.android.ugc.trill" to "TikTok"
        )

        val filteredStats = rawStats
            .filter { it.totalTimeInForeground > 0 && it.packageName in allowedApps.keys }
            .groupBy { it.packageName }
            .mapValues { entry ->
                entry.value.sumOf { it.totalTimeInForeground }
            }

        val entries = ArrayList<PieEntry>()
        var totalSosmedMinutes = 0

        for ((packageName, totalTime) in filteredStats) {
            val label = allowedApps[packageName] ?: getAppNameFromPackage(packageName)
            val timeInMin = (totalTime / 1000 / 60).toInt()
            val hours = timeInMin / 60
            val minutes = timeInMin % 60
            val displayLabel = if (hours > 0) "$label ${hours}j ${minutes}m" else "$label ${minutes}m"

            entries.add(PieEntry(timeInMin.toFloat(), displayLabel))
            totalSosmedMinutes += timeInMin
        }

        val prefs = getSharedPreferences("usage_data", Context.MODE_PRIVATE)
        prefs.edit().putInt("sosmed_minutes", totalSosmedMinutes).apply()

        val sosmedText = if (totalSosmedMinutes >= 60) {
            val hours = totalSosmedMinutes / 60
            val minutes = totalSosmedMinutes % 60
            "$hours j $minutes menit"
        } else {
            "Total Sosial Media: $totalSosmedMinutes menit"
        }
        totalSosmedUsageText.text = sosmedText

        val dataSet = PieDataSet(entries, "Penggunaan Aplikasi (menit)").apply {
            colors = listOf(
                ContextCompat.getColor(this@HomeActivity, R.color.green),
                ContextCompat.getColor(this@HomeActivity, R.color.yellow),
                ContextCompat.getColor(this@HomeActivity, R.color.primary),
                ContextCompat.getColor(this@HomeActivity, R.color.orange)
            )
            sliceSpace = 3f
            selectionShift = 5f
        }

        val pieData = PieData(dataSet).apply {
            setDrawValues(true)
            setValueTextSize(16f)
            setValueTextColor(Color.BLACK)
        }

        pieChart.apply {
            data = pieData
            setUsePercentValues(false)
            description.isEnabled = false
            setEntryLabelColor(Color.BLACK)
            setDrawEntryLabels(false)
            setDrawHoleEnabled(true)
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleAlpha(110)
            setEntryLabelTextSize(14f)
            legend.apply {
                isEnabled = true
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                isWordWrapEnabled = true
                textSize = 14f
                textColor = Color.BLACK
            }
            invalidate()
        }

        val pm = packageManager
        val appUsageList = mutableListOf<AppUsageInfo>()

        for (usageStats in rawStats) {
            try {
                val packageName = usageStats.packageName
                val totalTime = usageStats.totalTimeInForeground

                if (totalTime > 60000) {  // 60000 ms = 1 menit
                    val appName = pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
                    val icon = pm.getApplicationIcon(packageName)
                    appUsageList.add(AppUsageInfo(appName, packageName, icon, totalTime))
                }
            } catch (e: PackageManager.NameNotFoundException) {
                continue
            }
        }

        return appUsageList.sortedByDescending { it.usageTime }
    }

    private fun getAppNameFromPackage(packageName: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "Unknown"
        }
    }
}