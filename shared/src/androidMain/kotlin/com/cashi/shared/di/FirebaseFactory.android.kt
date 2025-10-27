package com.cashi.shared.di

import com.cashi.shared.data.local.FirebaseFirestoreClient

actual fun createFirebaseFirestoreClient(): FirebaseFirestoreClient {
    return FirebaseFirestoreClient()
}


