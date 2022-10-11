package com.example.screp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.screp.bluetoothService.BluetoothServiceManager
import com.example.screp.bottomNavigation.BottomNavigation
import com.example.screp.bottomNavigation.NavigationGraph
import com.example.screp.data.Settings
import com.example.screp.sensorService.SensorDataManager
import com.example.screp.ui.theme.ScrepTheme
import com.example.screp.viewModels.PhotoAndMapViewModel
import com.example.screp.viewModels.StepCountViewModel
import com.example.screp.viewModels.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {
    companion object {
        private lateinit var stepCountViewModel: StepCountViewModel
        private lateinit var weatherViewModel: WeatherViewModel
        private lateinit var photoAndMapViewModel: PhotoAndMapViewModel
        private lateinit var bluetoothServiceManager: BluetoothServiceManager
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        val STEP_GOAL = stringPreferencesKey("stepGoal")
        val NOTIFICATION_TIME = stringPreferencesKey("notificationTime")
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var dataManager: SensorDataManager


    lateinit var takePermissions: ActivityResultLauncher<Array<String>>
    lateinit var takeResultLauncher: ActivityResultLauncher<Intent>
    lateinit var pairPermissionResultLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        dataManager = SensorDataManager(this)

        super.onCreate(savedInstanceState)

        bluetoothServiceManager = BluetoothServiceManager(this, this)
        hasLocationPermissions()
        getActivityPermission()

        // initiate bluetoothService
        bluetoothServiceManager.init()
        getBluetoothPermission()



        stepCountViewModel = StepCountViewModel(application)
        weatherViewModel = WeatherViewModel()
        weatherViewModel.fetchWeatherData()
        photoAndMapViewModel = PhotoAndMapViewModel(application)


        // Get path to save and get photos
        val imgPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // setup data shared preferences
        val context = this
        val settings: Flow<Settings> = context.dataStore.data.map { preferences ->
            Settings(
                stepGoal = preferences[STEP_GOAL] ?: "5000",
                notificationTime = preferences[NOTIFICATION_TIME] ?: "5:00"
            )
        }


        setContent {
            ScrepTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()


                    Scaffold(
                        bottomBar = {
                            if (navBackStackEntry?.destination?.route != "edit") {
                                BottomNavigation(navController = navController) }
                            }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            NavigationGraph(
                                navController = navController,
                                stepCountViewModel = stepCountViewModel,
                                weatherViewModel = weatherViewModel,
                                photoAndMapViewModel = photoAndMapViewModel,
                                imgPath = imgPath,
                                fusedLocationProviderClient = fusedLocationProviderClient,
                                dataManager = dataManager,
                                preferenceDataStore = context.dataStore,
                                settings = settings,
                                STEP_GOAL = STEP_GOAL,
                                NOTIFICATION_TIME = NOTIFICATION_TIME,
                                bluetoothServiceManager = bluetoothServiceManager
                            )
                        }
                    }
                }
            }
        }
    }

    private fun hasLocationPermissions(): Boolean {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("aaaaaa", "No gps access")
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION ), 1);
            return true // assuming that the user grants permission
        } else if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d("aaaaaa", "gps access")
        }
        return true
    }

    private fun getActivityPermission(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 1)
        }
    }

    private fun getBluetoothPermission(){
        // Get BT permissions
        takePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
            {
                it.entries.forEach{
                    Log.d("BT_LOG", " list permission" + "${it.key} = ${it.value}")
                    if (it.value == false) {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        takeResultLauncher.launch(enableBtIntent)
                    }
                }
                if (it[Manifest.permission.BLUETOOTH_ADMIN] == true
                    && it[Manifest.permission.ACCESS_FINE_LOCATION] == true){
                    Log.d("BT_LOG", "bluetooth and location access OK")
                } else {
                    Toast.makeText(applicationContext, "Bluetooth permissions are not granted", Toast.LENGTH_SHORT).show()
                }
            }
        takeResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback{
                    result -> if (result.resultCode == RESULT_OK){
                Log.d("BT_LOG", "activity result callback ${result.resultCode}")
            } else {
                Log.d("BT_LOG", "activity result callback ${result.resultCode}")
            }
            })

        takePermissions.launch(arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        ))

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(bluetoothServiceManager.receiver, filter)

    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothServiceManager.receiver)
    }

}