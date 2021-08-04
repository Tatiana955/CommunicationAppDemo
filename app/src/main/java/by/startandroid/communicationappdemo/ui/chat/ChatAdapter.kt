package by.startandroid.communicationappdemo.ui.chat

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.*
import by.startandroid.communicationappdemo.R
import by.startandroid.communicationappdemo.data.ChatMessage
import by.startandroid.communicationappdemo.databinding.ImageLayoutBinding
import by.startandroid.communicationappdemo.databinding.MessageLayoutBinding
import by.startandroid.communicationappdemo.ui.chat.ChatFragment.Companion.ANONYMOUS
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ChatAdapter(
        private val options: FirebaseRecyclerOptions<ChatMessage>,
        private val currentUserName: String?
): FirebaseRecyclerAdapter<ChatMessage, ViewHolder>(options) {

    inner class MessageViewHolder(private val binding: MessageLayoutBinding): ViewHolder(binding.root) {

        fun bind(item: ChatMessage) {
            binding.messageTextView.text = item.text
            setTextColor(item.name, binding.messageTextView)

            binding.messengerName.text = if (item.name == null) ANONYMOUS else item.name
            if (item.photoUrl != null) {
                loadImageIntoView(binding.messengerImageView, item.photoUrl!!)
            } else {
                binding.messengerImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
            }
        }

        private fun setTextColor(userName: String?, textView: TextView) {
            if (userName != ANONYMOUS && currentUserName == userName && userName != null) {
                textView.setBackgroundResource(R.drawable.rounded_message_red)
                textView.setTextColor(Color.WHITE)
            } else {
                textView.setBackgroundResource(R.drawable.rounded_message_gray)
                textView.setTextColor(Color.BLACK)
            }
        }
    }

    inner class ImageMessageViewHolder(private val binding: ImageLayoutBinding): ViewHolder(binding.root) {

        fun bind(item: ChatMessage) {
            item.imageUrl?.let { loadImageIntoView(binding.imageView, it) }
            binding.name.text = if (item.name == null) ANONYMOUS else item.name
            if (item.photoUrl != null) {
                loadImageIntoView(binding.messengerImageView, item.photoUrl!!)
            } else {
                binding.messengerImageView.setImageResource(R.drawable.ic_baseline_account_circle_24)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_TEXT) {
            val view = inflater.inflate(R.layout.message_layout, parent, false)
            val binding = MessageLayoutBinding.bind(view)
            MessageViewHolder(binding)
        } else {
            val view = inflater.inflate(R.layout.image_layout, parent, false)
            val binding = ImageLayoutBinding.bind(view)
            ImageMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ChatMessage) {
        if (options.snapshots[position].text != null) {
            (holder as MessageViewHolder).bind(model)
        } else {
            (holder as ImageMessageViewHolder).bind(model)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (options.snapshots[position].text != null) VIEW_TYPE_TEXT else VIEW_TYPE_IMAGE
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

    companion object {
        const val TAG = "!!!Adapter"
        const val VIEW_TYPE_TEXT = 1
        const val VIEW_TYPE_IMAGE = 2
    }
}