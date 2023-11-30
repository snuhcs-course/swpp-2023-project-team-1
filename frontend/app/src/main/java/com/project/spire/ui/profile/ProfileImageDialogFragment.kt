package com.project.spire.ui.profile

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import coil.load
import coil.transform.CircleCropTransformation
import com.example.spire.R

class ProfileImageDialogFragment(
    private val profileImageUrl: String
): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val dialog = activity.let {
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(requireActivity().layoutInflater.inflate(R.layout.fragment_profile_image_dialog, null))
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as AlertDialog?
        if (dialog != null) {
            dialog.findViewById<ImageView>(R.id.iv_profile_image)?.load(profileImageUrl) {
                crossfade(true)
            }
        }
    }
}