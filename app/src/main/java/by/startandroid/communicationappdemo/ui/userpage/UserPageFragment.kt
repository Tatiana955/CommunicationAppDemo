package by.startandroid.communicationappdemo.ui.userpage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import by.startandroid.communicationappdemo.data.ChatMessage
import by.startandroid.communicationappdemo.databinding.FragmentUserPageBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class UserPageFragment : Fragment() {
    private var _binding: FragmentUserPageBinding? = null
    private val binding get() = _binding!!
    private var user: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserPageBinding.inflate(inflater, container, false)
        user = Firebase.auth.currentUser
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val message = ChatMessage(
                null,
                getUserName(),
                getPhotoUrl(),
                null
        )

        binding.firstName.text = if (message.name == null) ANONYMOUS else message.name

        if (message.photoUrl != null) {
            loadImageIntoView(binding.photo, message.photoUrl!!)
        } else {
            binding.photo.drawable
        }
    }

    private fun getPhotoUrl(): String? {
        return user?.photoUrl?.toString()
    }

    private fun getUserName(): String? {
        return if (user != null) {
            user!!.displayName
        } else ANONYMOUS
    }

    private fun loadImageIntoView(view: ImageView, url: String) {
        if (url.startsWith("gs://")) {
            val storageReference = Firebase.storage.getReferenceFromUrl(url)
            storageReference.downloadUrl
                    .addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        Glide.with(view.context).load(downloadUrl).into(view)
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "Не удалось получить URL для загрузки.", e)
                    }
        } else {
            Glide.with(view.context).load(url).into(view)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "!!!UserPage"
        const val ANONYMOUS = "anonymous"
    }
}