package com.project.spire.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spire.databinding.FragmentNotificationsBinding
import com.project.spire.utils.NotificationsViewModelFactory

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    private val viewModelFactory = NotificationsViewModelFactory()
    private val notificationsViewModel: NotificationsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[NotificationsViewModel::class.java]
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nothingToShow: TextView = binding.nothingToShow
        val recyclerView = binding.notificationsRecyclerView
        val layoutManager = LinearLayoutManager(context)
        val adapter = NotificationAdapter(
            emptyList(),
            findNavController(),
            notificationsViewModel
        )
        recyclerView.run {
            this.layoutManager = layoutManager
            this.adapter = adapter
        }

        notificationsViewModel.getInitialNotifications()

        notificationsViewModel.text.observe(viewLifecycleOwner) {
            nothingToShow.text = it
        }

        notificationsViewModel.notifications.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.progressBar.visibility = View.GONE
                if (it.isEmpty()) {
                    nothingToShow.visibility = View.VISIBLE
                } else {
                    binding.notificationsCount.text = "(${it.size})"
                    nothingToShow.visibility = View.GONE
                }
                adapter.updateList(it)
            }
        }

        notificationsViewModel.removeAt.observe(viewLifecycleOwner) {
            if (it != -1) {
                adapter.removeAt(it)
                binding.notificationsCount.text = "(${adapter.itemCount})"
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Loads more posts when the user scrolls to the bottom of the list
                if (!recyclerView.canScrollVertically(1)) {
                    notificationsViewModel.getMoreNotifications()
                }
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            notificationsViewModel.getInitialNotifications()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}