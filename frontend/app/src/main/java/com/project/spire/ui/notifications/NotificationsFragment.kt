package com.project.spire.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spire.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val nothingToShow: TextView = binding.nothingToShow
        val recyclerView = binding.notificationsRecyclerView
        val layoutManager = LinearLayoutManager(context)
        val adapter = NotificationAdapter(notificationsViewModel.notifications.value!!)
        recyclerView.run {
            this.layoutManager = layoutManager
            this.adapter = adapter
        }

        notificationsViewModel.text.observe(viewLifecycleOwner) {
            nothingToShow.text = it
        }

        notificationsViewModel.notifications.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                nothingToShow.visibility = View.VISIBLE
            } else {
                binding.notificationsCount.text = "(${it.size})"
                nothingToShow.visibility = View.GONE
            }
            adapter.notificationList = it
            adapter.notifyDataSetChanged()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}