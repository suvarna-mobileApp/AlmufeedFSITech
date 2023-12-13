package com.android.almufeed.ui.launchpad

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.almufeed.R
import com.android.almufeed.business.domain.utils.collectLatestFlow
import com.android.almufeed.business.domain.utils.exhaustive
import com.android.almufeed.business.domain.utils.toast
import com.android.almufeed.databinding.ActivityLaunchpadBinding
import com.android.almufeed.ui.base.BaseActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.nav_header_main.view.*
import javax.inject.Inject

@AndroidEntryPoint
class LaunchpadActivity : BaseActivity() {

    private val launchpadViewModel: LaunchpadViewModel by viewModels()
    private lateinit var binding: ActivityLaunchpadBinding
    private lateinit var snack: Snackbar
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    @Inject
    lateinit var glideRequestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchpadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            progressBarHome.visibility = View.GONE
            // textLaunch.text = "This is Launchpad Activity"
            snack = Snackbar.make(
                root,
                getString(R.string.text_network_not_available),
                Snackbar.LENGTH_INDEFINITE
            )
        }
        setSupportActionBar(binding.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navViewLaunchpad
        navController = findNavController(R.id.nav_host_fragment_content_app)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar!!.title = "${resources.getString(R.string.menu_launchpad)}"
        subscribeObservers()

        binding.navViewLaunchpad.setNavigationItemSelectedListener { item ->
            navigate(item.itemId)
            true
        }
    }

    private fun navigate(itemId: Int) {
        when (itemId) {
            R.id.nav_description -> {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                if (navController.currentDestination!!.id != R.id.nav_description)
                    navController.navigate(R.id.nav_description)
            }
          /*  R.id.nav_profile -> {
                if (navController.currentDestination!!.id != R.id.nav_profile)
                    navController.navigate(R.id.nav_profile)
            }*/
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else if(navController.currentDestination!!.id == R.id.nav_home){
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun subscribeObservers() {
        @Suppress("IMPLICIT_CAST_TO_ANY")
        collectLatestFlow(launchpadViewModel.taskEvent) { event ->
            when (event) {
                is TaskEvent.SetUserDetails -> {
                    val navHead = binding.navViewLaunchpad.getHeaderView(0)
                    Log.e("LAUNCH::", "UI SetUserDetails name : ${event.name}")
                    navHead.nav_user_name.text = event.name
                    val mob = "+971 ${event.mobileNumber}"
                    navHead.nav_user_contact.text = mob
                    navHead.nav_user_email.text = event.email

                    Glide.with(this)
                        .load(event.userProfilePic)
                        .placeholder(R.drawable.ic_profile_circle)
                        .error(R.drawable.ic_profile_circle)
                        .into(navHead.img_profile_pic)
                }
                else -> {}
            }.exhaustive
        }
    }

    override fun showNetworkSnackBar(isNetworkAvailable: Boolean) {
        if (isNetworkAvailable) {
            Log.e("LAU::", "connectionLiveData: true")
            if (snack.isShown) {
                this.toast(getString(R.string.text_network_connected))
                snack.dismiss()
            }

        } else {
            Log.e("LAU::", "connectionLiveData: false")
            snack.show()
        }
    }
}