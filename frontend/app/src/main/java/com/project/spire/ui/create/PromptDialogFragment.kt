package com.project.spire.ui.create

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.example.spire.R


class PromptDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val dialog = activity.let {
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(requireActivity().layoutInflater.inflate(R.layout.fragment_prompt_dialog, null))
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

        // Make dialog not dismissable when user touches outside of dialog
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window?.setDimAmount(0.5f)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as AlertDialog?
        if (dialog != null) {
            dialog.findViewById<AppCompatButton>(R.id.cancel_button)?.setOnClickListener {
                dialog.dismiss()
            }
            dialog.findViewById<AppCompatButton>(R.id.generate_button)?.setOnClickListener {
                generateImage()
            }
        }
    }

    private fun generateImage() {
        val prompt = dialog?.findViewById<EditText>(R.id.prompt_input)?.text
        if (prompt != null) {
            // TODO: Generate image with prompt



        } else {
            Log.e("PromptDialogFragment", "Prompt is null")
        }
    }
}