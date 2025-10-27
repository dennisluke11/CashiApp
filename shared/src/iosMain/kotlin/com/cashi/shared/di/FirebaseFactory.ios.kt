package com.cashi.shared.di

import com.cashi.shared.data.local.FirebaseFirestoreClient

/**
 * iOS implementation of Firebase Firestore client factory
 * Uses iOS Firebase SDK
 */
actual fun createFirebaseFirestoreClient(): FirebaseFirestoreClient {
    return FirebaseFirestoreClient()
}

