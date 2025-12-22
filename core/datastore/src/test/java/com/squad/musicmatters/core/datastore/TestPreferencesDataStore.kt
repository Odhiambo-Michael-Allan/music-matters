package com.squad.musicmatters.core.datastore

import androidx.datastore.core.DataStoreFactory
import kotlinx.coroutines.CoroutineScope
import org.junit.rules.TemporaryFolder

fun TemporaryFolder.testUserPreferencesDataStore(
    coroutineScope: CoroutineScope,
    userPreferencesSerializer: UserPreferencesSerializer = UserPreferencesSerializer()
) = DataStoreFactory.create(
    serializer = userPreferencesSerializer,
    scope = coroutineScope
) {
    newFile( "user_preferences_test.pb" )
}