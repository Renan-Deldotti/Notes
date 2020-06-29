package br.com.renandeldotti.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.customview.widget.Openable
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import br.com.renandeldotti.notes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object{
        const val notesAppPreferences:String = "br.com.renandeldotti.notes.sharedPrefs"
    }

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(mainBinding.root)

        navController = Navigation.findNavController(this, R.id.main_navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }
}