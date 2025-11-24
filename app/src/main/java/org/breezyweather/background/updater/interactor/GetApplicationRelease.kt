/*
 * This file is part of Breezy Weather.
 *
 * Breezy Weather is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, version 3 of the License.
 *
 * Breezy Weather is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Breezy Weather. If not, see <https://www.gnu.org/licenses/>.
 */

package org.breezyweather.background.updater.interactor

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.breezyweather.background.updater.data.ReleaseService
import org.breezyweather.background.updater.model.Release
import org.breezyweather.domain.settings.SettingsManager
import android.util.Log
import java.util.Date
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

/**
 * Taken from Mihon
 * Apache License, Version 2.0
 *
 * https://github.com/mihonapp/mihon/blob/aa498360db90350f2642e6320dc55e7d474df1fd/domain/src/main/java/tachiyomi/domain/release/interactor/GetApplicationRelease.kt
 */
class GetApplicationRelease @Inject constructor(
    @ApplicationContext val context: Context,
    val service: ReleaseService,
) {

    suspend fun await(
        arguments: Arguments,
    ): Result {
        val now = Date().time

        val lastChecked = SettingsManager.getInstance(context).appUpdateCheckLastTimestamp

        // Limit checks to once every day at most
        if (!arguments.forceCheck && now < lastChecked + 1.days.inWholeMilliseconds) {
            return Result.NoNewUpdate
        }

        val isPrerelease = arguments.versionName.contains(Regex("beta|alpha|rc", RegexOption.IGNORE_CASE))
        val release = try {
            service.latest(arguments.org, arguments.repository, onlyStable = !isPrerelease)
        } catch (e: Exception) {
            Log.w("AppUpdateChecker", "Failed to fetch latest release", e)
            SettingsManager.getInstance(context).appUpdateCheckLastTimestamp = now
            return Result.NoNewUpdate
        }

        SettingsManager.getInstance(context).appUpdateCheckLastTimestamp = now

        // Check if latest version is different from current version
        val isNewVersion = isNewVersion(
            arguments.versionName,
            release.version
        )
        return when {
            isNewVersion -> Result.NewUpdate(release)
            else -> Result.NoNewUpdate
        }
    }

    private fun isNewVersion(
        versionName: String,
        versionTag: String,
    ): Boolean {
        // Removes "v" prefixes and non-numeric/dot chars for basic comparison
        val newVersion = versionTag.replace("[^\\d.]".toRegex(), "")
        val oldVersion = versionName.replace("[^\\d.]".toRegex(), "")

        val newParts = newVersion.split(".").mapNotNull { it.toIntOrNull() }
        val oldParts = oldVersion.split(".").mapNotNull { it.toIntOrNull() }

        val length = maxOf(newParts.size, oldParts.size)
        for (i in 0 until length) {
            val newPart = newParts.getOrElse(i) { 0 }
            val oldPart = oldParts.getOrElse(i) { 0 }
            if (newPart > oldPart) return true
            if (newPart < oldPart) return false
        }

        // If numeric parts are identical, check if we are upgrading from prerelease to stable
        // e.g. 6.0.0-beta -> 6.0.0
        val oldIsPrerelease = versionName.contains(Regex("beta|alpha|rc", RegexOption.IGNORE_CASE))
        val newIsPrerelease = versionTag.contains(Regex("beta|alpha|rc", RegexOption.IGNORE_CASE))

        if (oldIsPrerelease && !newIsPrerelease) {
            return true
        }

        return false
    }

    data class Arguments(
        val versionName: String,
        val org: String,
        val repository: String,
        val forceCheck: Boolean = false,
    )

    sealed interface Result {
        data class NewUpdate(val release: Release) : Result
        data object NoNewUpdate : Result
        data object OsTooOld : Result
    }
}
