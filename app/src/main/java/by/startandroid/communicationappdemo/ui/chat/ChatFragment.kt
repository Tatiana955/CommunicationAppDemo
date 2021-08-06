package by.startandroid.communicationappdemo.ui.chat

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.startandroid.communicationappdemo.*
import by.startandroid.communicationappdemo.R
import by.startandroid.communicationappdemo.data.ChatMessage
import by.startandroid.communicationappdemo.databinding.FragmentChatBinding
import by.startandroid.communicationappdemo.ui.chat.dialog.DialogFragment
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private var navController: NavController? = null
    private lateinit var adapter: ChatAdapter
    private lateinit var manager: LinearLayoutManager
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val document = registerForActivityResult(DocumentContract()) { uri ->
        onImageSelected(uri)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        database = Firebase.database
        initAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        startLogin()

        adapter.registerAdapterDataObserver(ScrollToBottomObserver(binding.messageRecView, adapter, manager))

        binding.addMessage.addTextChangedListener(ButtonObserver(binding.sendButton))

        binding.sendButton.setOnClickListener {
            val message = ChatMessage(
                binding.addMessage.text.toString(),
                getUserName(),
                getPhotoUrl(),
                null
            )
            database.reference.child(MESSAGES_CHILD).push().setValue(message)
            binding.addMessage.setText("")
        }

        binding.addImageView.setOnClickListener {
            document.launch(arrayOf("image/*"))
        }
    }

    override fun onStart() {
        super.onStart()
        startLogin()
    }

    private fun startLogin() {
        // Check if user is signed in.
        if (auth.currentUser == null) {
            navController?.navigate(R.id.signInFragment)
            return
        }
    }

    private fun initAdapter() {
        val reference = database.reference.child(MESSAGES_CHILD)
        val options = FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(reference, ChatMessage::class.java)
                .build()
        adapter = ChatAdapter(options, getUserName(), this)
        binding.progressBar.visibility = ProgressBar.INVISIBLE
        manager = LinearLayoutManager(requireContext())
        manager.stackFromEnd = true
        binding.messageRecView.layoutManager = manager
        binding.messageRecView.adapter = adapter
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    private fun onImageSelected(uri: Uri?) {
        val user = auth.currentUser
        val message = ChatMessage(null, getUserName(), getPhotoUrl(), LOADING_IMAGE_URL)

        database.reference
            .child(MESSAGES_CHILD)
            .push()
            .setValue(
                    message,
                    DatabaseReference.CompletionListener { databaseError, databaseReference ->
                        if (databaseError != null) {
                            Log.d(TAG, "Невозможно записать сообщение в базу данных.", databaseError.toException())
                            return@CompletionListener
                        }

                        // Build a StorageReference and then upload the file
                        val key = databaseReference.key
                        val storageReference = uri?.let {
                            Firebase.storage
                                    .getReference(user!!.uid)
                                    .child(key!!)
                                    .child(it.lastPathSegment!!)
                        }
                        if (storageReference != null) {
                            putImageInStorage(storageReference, uri, key)
                        }
                    })
    }

    private fun putImageInStorage(storageReference: StorageReference, uri: Uri, key: String?) {
        // First upload the image to Cloud Storage
        storageReference.putFile(uri)
                .addOnSuccessListener(requireActivity()) { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { uri ->
                                val message = ChatMessage(null, getUserName(), getPhotoUrl(), uri.toString())
                                database.reference
                                        .child(MESSAGES_CHILD)
                                        .child(key!!)
                                        .setValue(message)
                            }
                }
                .addOnFailureListener(requireActivity()) { e ->
                    Log.d(TAG, "Не удалось загрузить изображение.", e)
                    Snackbar.make(requireView(), R.string.failed_to_load_image, Snackbar.LENGTH_SHORT).show()
                }
    }

    private fun getPhotoUrl(): String? {
        val user = auth.currentUser
        return user?.photoUrl?.toString()
    }

    private fun getUserName(): String? {
        val user = auth.currentUser
        return if (user != null) {
            user.displayName
        } else ANONYMOUS
    }

    fun openDialog() {
        DialogFragment().show(childFragmentManager, DialogFragment.TAG)
    }

    fun deleteMessage(position: Int) {
        adapter.getRef(position).removeValue()
        Snackbar.make(requireView(), R.string.message_delete, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "!!!Chat"
        const val MESSAGES_CHILD = "messages"
        const val ANONYMOUS = "anonymous"
        private const val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    }
}