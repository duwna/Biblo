package com.duwna.biblo.di

import android.content.Context
import com.duwna.biblo.data.UploadImageManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore.apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): StorageReference = Firebase.storage.reference

    @Provides
    @Singleton
    fun provideUploadImageManager(
        @ApplicationContext context: Context,
        storage: StorageReference
    ): UploadImageManager {
        return UploadImageManager(context, storage)
    }
}

