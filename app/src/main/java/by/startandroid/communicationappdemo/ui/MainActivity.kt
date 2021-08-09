package by.startandroid.communicationappdemo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import by.startandroid.communicationappdemo.R
import by.startandroid.communicationappdemo.databinding.ActivityMainBinding
import by.startandroid.communicationappdemo.ui.chat.ChatFragment
import by.startandroid.communicationappdemo.ui.chat.dialog.DialogFragment
import by.startandroid.communicationappdemo.ui.chat.dialog.DialogFragmentListener
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), DialogFragmentListener {
    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = supportFragmentManager.findFragmentById(R.id.navHost)?.findNavController()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_menu -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this)
        navController?.navigate(R.id.signInFragment)
    }

    override fun clearMessageClick(dialog: DialogFragment) {
        val database = Firebase.database
        database.reference.child(ChatFragment.MESSAGES_CHILD).removeValue()
        dialog.dismiss()
    }
}