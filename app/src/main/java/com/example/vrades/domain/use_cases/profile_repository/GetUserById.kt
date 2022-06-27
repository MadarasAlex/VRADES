package com.example.vrades.domain.use_cases.profile_repository

import com.example.vrades.domain.repositories.ProfileRepository

class GetUserById(private val repository: ProfileRepository) {
    suspend operator fun invoke() = repository.getUserById()
}