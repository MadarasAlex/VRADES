package com.example.vrades.presentation.ui.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.vrades.R
import com.example.vrades.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var job: Job
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = lifecycleScope.launch(Dispatchers.Main) {
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding.apply {
            val bottomNavigationView = bnNavigationTest
            val navHostFragment = navHostFragment.getFragment<NavHostFragment>()
            val navController = navHostFragment.navController
            navController.addOnDestinationChangedListener { navC: NavController, _: NavDestination, _: Bundle? ->
                if (navC.currentDestination!!.id != R.id.nav_face
                    && navC.currentDestination!!.id != R.id.nav_audio
                    && navC.currentDestination!!.id != R.id.nav_writing
                ) {
                    val set = ConstraintSet()
                    set.clone(flMain)
                    set.constrainPercentHeight(R.id.nav_host_fragment, 1F)
                    set.applyTo(flMain)
                    bnNavigationTest.visibility = View.INVISIBLE
                } else {
                    val set = ConstraintSet()
                    set.clone(flMain)
                    set.constrainPercentHeight(R.id.nav_host_fragment, 0.92F)
                    set.applyTo(flMain)
                    bottomNavigationView.visibility = View.VISIBLE
                    bottomNavigationView.menu.forEach { item ->
                        item.isEnabled = false
                    }
                    bottomNavigationView.setupWithNavController(navController)
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBackPressed() {
        val navHostFragment = binding.navHostFragment.getFragment<NavHostFragment>()
        val currentFragment = navHostFragment.navController.currentDestination?.id
        if (currentFragment == R.id.nav_audio || currentFragment == R.id.nav_face || currentFragment == R.id.nav_writing) {
            return
        }
        super.onBackPressed()
    }

}