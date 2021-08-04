package by.startandroid.communicationappdemo.ui.signin

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import by.startandroid.communicationappdemo.R
import by.startandroid.communicationappdemo.databinding.FragmentSignInBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private var navController: NavController? = null
    private lateinit var auth: FirebaseAuth

    private val signIn: ActivityResultLauncher<Intent> =
        registerForActivityResult(FirebaseAuthUIActivityResultContract(), this::onSignInResult)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        if (Firebase.auth.currentUser == null) {
            // See: https://firebase.google.com/docs/auth/android/firebaseui
            val intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.mipmap.ic_cruelty)
                .setAvailableProviders(listOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                ))
                .build()
            signIn.launch(intent)
        } else {
            goToChatFrag()
        }
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "Вход выполнен успешно!")
            Snackbar.make(requireView(), R.string.login_completed_successfully, Snackbar.LENGTH_SHORT).show()
            goToChatFrag()
        } else {
            Snackbar.make(requireView(), R.string.an_error_occurred_while_logging_in, Snackbar.LENGTH_SHORT).show()
            val response = result.idpResponse
            if (response == null) {
                Log.d(TAG, "Вход отменен")
                Snackbar.make(requireView(), R.string.login_canceled, Snackbar.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "Ошибка входа", response.error)
                Snackbar.make(requireView(), R.string.login_failed, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToChatFrag() {
        navController?.navigate(R.id.chatFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "!!!SignIn"
    }
}