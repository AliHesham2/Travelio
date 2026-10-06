package com.aly.travelio.ui.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import com.aly.travelio.data.remote.firebase.FirebaseSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class App : Application()