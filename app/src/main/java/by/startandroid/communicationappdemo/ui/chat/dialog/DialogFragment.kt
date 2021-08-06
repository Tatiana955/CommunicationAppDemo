package by.startandroid.communicationappdemo.ui.chat.dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.startandroid.communicationappdemo.databinding.FragmentDialogBinding
import by.startandroid.communicationappdemo.ui.chat.ChatFragment.Companion.MESSAGES_CHILD
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

open class DialogFragment : DialogFragment() {
    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentDialogBinding.inflate(inflater, container, false)
        dialog?.setTitle("Dialog")
        database = Firebase.database
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clearImageView.setOnClickListener {
            clear()
        }
    }

    private fun clear() {
        database.reference.child(MESSAGES_CHILD).removeValue()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "!!!Dialog"
    }
}