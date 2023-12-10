package com.project.spire.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.spire.core.auth.AuthRepository
import com.project.spire.core.inference.InferenceRepository
import com.project.spire.core.inference.SegmentationRepository
import com.project.spire.core.search.SearchRepository
import com.project.spire.core.user.UserRepository
import com.project.spire.ui.auth.LoginViewModel
import com.project.spire.ui.auth.SignUpViewModel
import com.project.spire.ui.auth.VerifyEmailViewModel
import com.project.spire.ui.create.CanvasViewModel
import com.project.spire.ui.create.InferenceViewModel
import com.project.spire.ui.feed.FeedViewModel
import com.project.spire.ui.feed.PostViewModel
import com.project.spire.ui.notifications.NotificationsViewModel
import com.project.spire.ui.profile.ProfileViewModel
import com.project.spire.ui.relationship.RelationshipViewModel
import com.project.spire.ui.search.SearchViewModel

class LoginViewModelFactory(
    private val repository: AuthRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SignUpViewModelFactory(
    private val repository: AuthRepository,
) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class VerifyEmailViewModelFactory(
    private val repository: AuthRepository,
) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VerifyEmailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VerifyEmailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CanvasViewModelFactory(
    private val segmentationRepository: SegmentationRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CanvasViewModel::class.java)) {
            return CanvasViewModel(segmentationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class InferenceViewModelFactory(
    private val inferenceRepository: InferenceRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InferenceViewModel::class.java)) {
            return InferenceViewModel(inferenceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class FeedViewModelFactory(
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            return FeedViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class PostViewModelFactory(
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class NotificationsViewModelFactory(
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
            return NotificationsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ProfileViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(authRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class RelationshipViewModelFactory(
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RelationshipViewModel::class.java)) {
            return RelationshipViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SearchViewModelFactory(
    private val searchRepository: SearchRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(searchRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}