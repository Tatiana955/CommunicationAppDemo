package by.startandroid.communicationappdemo

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

// See: https://developer.android.com/reference/androidx/activity/result/contract/ActivityResultContracts.OpenDocument
class DocumentContract: ActivityResultContracts.OpenDocument() {

    override fun createIntent(context: Context, input: Array<out String>): Intent {
        val intent = super.createIntent(context, input)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        return intent
    }
}