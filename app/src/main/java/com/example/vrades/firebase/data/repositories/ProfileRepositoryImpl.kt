package com.example.vrades.firebase.repositories.data

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.vrades.firebase.repositories.domain.ProfileRepository
import com.example.vrades.model.LifeHack
import com.example.vrades.model.Response
import com.example.vrades.model.Response.Success
import com.example.vrades.model.Test
import com.example.vrades.model.User
import com.example.vrades.utils.Constants
import com.example.vrades.utils.Constants.ADVICES
import com.example.vrades.utils.Constants.DATE
import com.example.vrades.utils.Constants.DETAILS
import com.example.vrades.utils.Constants.EMAIL
import com.example.vrades.utils.Constants.IMAGE
import com.example.vrades.utils.Constants.IS_COMPLETED
import com.example.vrades.utils.Constants.IS_TUTORIAL_ENABLED
import com.example.vrades.utils.Constants.LIFEHACKS_NAME_REF
import com.example.vrades.utils.Constants.LIFEHACKS_REF
import com.example.vrades.utils.Constants.NAME
import com.example.vrades.utils.Constants.RESULT
import com.example.vrades.utils.Constants.STATE
import com.example.vrades.utils.Constants.TEST
import com.example.vrades.utils.Constants.TESTS
import com.example.vrades.utils.Constants.TRIGGER_EMOTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

class ProfileRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    @Named(Constants.USERS_REF) private val usersRef: DatabaseReference,
    @Named(Constants.USER_NAME_REF) private val usersNameRef: DatabaseReference,
    private val database: FirebaseDatabase
) : ProfileRepository {

    override suspend fun getUserById(): Flow<Response<User>> = flow {
        try {
            emit(Response.Loading)
            val lifeHacks = mutableListOf<LifeHack>()
            val tests = mutableListOf<Test>()
            println("users: ${usersRef.get().await().children}")
            usersRef.child(auth.currentUser!!.uid).get().await().apply {
                child(ADVICES).children.forEach { lifeHack ->
                    lifeHacks.add(
                        LifeHack(
                            lifeHack.key!!,
                            lifeHack.child(IMAGE).getValue(String::class.java)!!,
                            lifeHack.child(DETAILS).getValue(String::class.java)!!
                        )
                    )
                }
                child(TESTS).children.forEach { test ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        tests.add(
                            Test(
                              test.child(DATE).getValue(String::class.java),
                                test.child(STATE).getValue(Int::class.java)!!,
                                test.child(RESULT).getValue(String::class.java)!!,
                                test.child(IS_COMPLETED).getValue(Boolean::class.java)!!
                            )
                        )
                    }
                }.also {
                    val user = User(
                        child(EMAIL).getValue(String::class.java)!!,
                        child(NAME).getValue(String::class.java)!!,
                        child(IMAGE).getValue(String::class.java)!!,
                        child(IS_TUTORIAL_ENABLED).getValue(Boolean::class.java),
                        lifeHacks,
                        tests
                    )
                    emit(Success(user))
                }



            }
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: Constants.ERROR_REF))
        }
    }

    override suspend fun getUserNameById(): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            usersNameRef.child(auth.currentUser!!.uid).get().await().getValue(String::class.java)
                .also {
                    emit(Success(it!!))
                }
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: Constants.ERROR_REF))
        }
    }

    override suspend fun setProfilePictureInStorage(picture: Uri): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            storage.getReference("Users/" + auth.currentUser!!.uid).putFile(picture).await().also {
                val location = storage.getReferenceFromUrl("Users/" + auth.currentUser!!.uid).path
                emit(Success(location))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: Constants.ERROR_REF))
        }
    }

    override suspend fun updateProfilePictureInRealtime(picture: String): Flow<Response<Boolean>> =
        flow {
            try {
                emit(Response.Loading)
                usersRef.child(auth.currentUser!!.uid).child(IMAGE).setValue(picture).await().also {
                    emit(Success(true))
                }
            } catch (e: Exception) {
                emit(Response.Error(e.message ?: Constants.ERROR_REF))
            }
        }

    override suspend fun addTestInRealtime(test: Test): Flow<Response<Boolean>> = flow {
        try {
            emit(Response.Loading)
            var k = 0
            val index =
                usersRef.child(auth.currentUser!!.uid).child(TESTS).get().await().children.forEach { _ ->
                    k++
                }
            print(index)
            usersRef.child(auth.currentUser!!.uid).child(TESTS).child(TEST + (k+1).toString())
                .setValue(test).await().also {
                    emit(Success(true))
                }

        } catch (e: Exception) {
            emit(Response.Error(e.message ?: Constants.ERROR_REF))
        }
    }

    override suspend fun generateAdvicesByTestResult(): Flow<Response<Boolean>> =flow{
        try {
            emit(Response.Loading)
            lateinit var lastResult: String
            lateinit var advice: LifeHack
            usersRef.child(auth.currentUser!!.uid).child(TESTS).get().await().children.forEach {
                lastResult = it.child(RESULT).getValue(String::class.java).toString()
            }
            database.reference.child(LIFEHACKS_REF).get().await().children.forEach {
                if(it.child(TRIGGER_EMOTION).getValue(String::class.java) == lastResult) {
                    advice = LifeHack(it.key!!, it.child(IMAGE).getValue(String::class.java)!!)
                }
            }
            usersRef.child(auth.currentUser!!.uid).child(ADVICES).setValue(advice).await().also {
                emit(Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: Constants.ERROR_REF))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getTestsByUserId(): Flow<Response<List<Test>>> = flow {
        try {
            emit(Response.Loading)
            val tests = mutableListOf<Test>()

            usersRef.child(auth.currentUser!!.uid).child(TESTS).get()
                .await().children.forEach { test ->
                    tests.add(
                        Test(
                            test.child(DATE).getValue(String::class.java),
                            test.child(STATE).getValue(Int::class.java)!!,
                            test.child(RESULT).getValue(String::class.java)!!,
                            test.child(IS_COMPLETED).getValue(Boolean::class.java)!!
                        )
                    )
                    emit(Success(tests))
                }
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: Constants.ERROR_REF))
        }
    }

    override suspend fun getAdvicesByUserId(): Flow<Response<List<LifeHack>>> = flow {
        try {
            emit(Response.Loading)
            val lifeHacks = mutableListOf<LifeHack>()
            usersRef.child(auth.currentUser!!.uid).child(ADVICES).get()
                .await().children.forEach { lifeHack ->
                    lifeHacks.add(
                        LifeHack(
                            lifeHack.key!!,
                            lifeHack.child(IMAGE).getValue(String::class.java)!!,
                            lifeHack.child(DETAILS).getValue(String::class.java)!!
                        )
                    )
                }
            emit(Success(lifeHacks))
            println("Here, received tests")

        } catch (e: Exception) {
            emit(Response.Error(e.message ?: Constants.ERROR_REF))
        }
    }

}