package by.startandroid.communicationappdemo.ui.chat.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import by.startandroid.communicationappdemo.R
import by.startandroid.communicationappdemo.databinding.FragmentDialogBinding
import java.lang.ClassCastException

open class DialogFragment : DialogFragment() {
    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: DialogFragmentListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentDialogBinding.inflate(inflater, container, false)
        dialog?.setTitle("Dialog")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clearImageView.setOnClickListener {
            listener.clearMessageClick(this)
            Toast.makeText(requireContext(), R.string.message_clear, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as DialogFragmentListener
        } catch (e: ClassCastException) {
            Log.d(TAG, e.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "!!!Dialog"
    }
}