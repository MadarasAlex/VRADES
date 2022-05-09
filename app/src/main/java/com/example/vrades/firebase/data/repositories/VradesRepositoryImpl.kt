package com.example.vrades.firebase.data.repositories

import com.example.vrades.firebase.repositories.domain.VradesRepository
import com.example.vrades.model.LifeHack
import com.example.vrades.model.Response
import com.example.vrades.utils.Constants
import com.example.vrades.utils.Constants.DETAILS
import com.example.vrades.utils.Constants.IMAGE
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

class VradesRepositoryImpl @Inject constructor(
    @Named(Constants.EMOTIONS_REF) private val emotionsRef: DatabaseReference,
    @Named(Constants.LIFEHACKS_REF) private val lifeHacksRef: DatabaseReference,
    @Named(Constants.DATA_AUDIO_TEST_REF) private val dataAudioTestRef: DatabaseReference,
    @Named(Constants.DATA_WRITING_TEST_REF) private val dataWritingTestRef: DatabaseReference,
    @Named(Constants.IMAGE_REF) private val imageRef: DatabaseReference
) : VradesRepository {
    override suspend fun getEmotions(): Flow<Response<List<String>>> = flow {
        try {
            emit(Response.Loading)
            val emotions = mutableListOf<String>()
            emotionsRef.get().await().children.forEach {
                emotions.add(it.key.toString())
            }
            emit(Response.Success(emotions))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: Constants.ERROR_REF))
        }

    }

    override suspend fun getLifeHacks(): Flow<Response<List<LifeHack>>> = flow {
        try {
            emit(Response.Loading)
            val lifeHacks = mutableListOf<LifeHack>()
            lifeHacksRef.get().await().children.forEach {
                lifeHacks.add(
                    LifeHack(
                        it.key!!, it.child(IMAGE).getValue(String::class.java)!!,
                        it.child(DETAILS).getValue(String::class.java)!!
                    )
                )
            }
            emit(Response.Success(lifeHacks))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: Constants.ERROR_REF))
        }
    }

    override suspend fun getDataAudioTest(): Flow<Response<List<String>>> = flow {
        try {
            emit(Response.Loading)
            val audioTestData = mutableListOf<String>()
            dataAudioTestRef.get().await().children.forEach {
                audioTestData.add(it.value.toString())
            }
            emit(Response.Success(audioTestData))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: Constants.ERROR_REF))
        }
    }

    override suspend fun getDataWritingTest(): Flow<Response<List<String>>> = flow {
        try {
            emit(Response.Loading)
            val writingTestData = mutableListOf<String>()
            dataWritingTestRef.get().await().children.forEach {
                writingTestData.add(it.value.toString())
            }
            emit(Response.Success(writingTestData))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: Constants.ERROR_REF))
        }
    }

    override suspend fun getPictureByName(name: String): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            imageRef.get().await().children.forEach {
                if (it.key == name) {
                    val image = it.getValue(String::class.java)
                    emit(Response.Success(image.toString()))
                }
            }

        } catch (e: Exception) {
            emit(Response.Error(e.message ?: Constants.ERROR_REF))
        }
    }

    override suspend fun getPictures(): Flow<Response<Map<String,String>>> = flow {
        try {
            emit(Response.Loading)
            val images = mutableMapOf<String,String>()
            imageRef.get().await().children.forEach {
                images[it.key!!] = it.getValue(String::class.java)!!
            }
            emit(Response.Success(images))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: Constants.ERROR_REF))
        }
    }
}