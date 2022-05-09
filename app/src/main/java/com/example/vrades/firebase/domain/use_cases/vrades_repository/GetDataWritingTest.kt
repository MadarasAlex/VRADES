package com.example.vrades.firebase.domain.use_cases.vrades_repository

import com.example.vrades.firebase.repositories.domain.VradesRepository

class GetDataWritingTest(private val repository: VradesRepository) {
    suspend operator fun invoke() = repository.getDataWritingTest()
}