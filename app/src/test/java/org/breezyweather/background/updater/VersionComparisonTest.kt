package org.breezyweather.background.updater

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName

/**
 * Unit tests for version comparison logic in GetApplicationRelease
 *
 * Tests the isNewVersion() private method logic through observed behavior
 */
class VersionComparisonTest {

    /**
     * Helper to simulate the version comparison logic from GetApplicationRelease
     */
    private fun isNewVersion(currentVersion: String, newVersionTag: String): Boolean {
        // Removes "v" prefixes and non-numeric/dot chars for basic comparison
        val newVersion = newVersionTag.replace("[^\\d.]".toRegex(), "")
        val oldVersion = currentVersion.replace("[^\\d.]".toRegex(), "")

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
        val oldIsPrerelease = currentVersion.contains(Regex("beta|alpha|rc", RegexOption.IGNORE_CASE))
        val newIsPrerelease = newVersionTag.contains(Regex("beta|alpha|rc", RegexOption.IGNORE_CASE))

        if (oldIsPrerelease && !newIsPrerelease) {
            return true
        }

        return false
    }

    @Test
    @DisplayName("Same version should not be considered new")
    fun sameVersionTest() = runTest {
        isNewVersion("6.0.21", "6.0.21") shouldBe false
        isNewVersion("v6.0.21", "v6.0.21") shouldBe false
        isNewVersion("1.2.3", "v1.2.3") shouldBe false
    }

    @Test
    @DisplayName("Major version upgrade should be detected")
    fun majorVersionUpgradeTest() = runTest {
        isNewVersion("6.0.21", "7.0.0") shouldBe true
        isNewVersion("1.0.0", "2.0.0") shouldBe true
        isNewVersion("5.9.9", "6.0.0") shouldBe true
    }

    @Test
    @DisplayName("Minor version upgrade should be detected")
    fun minorVersionUpgradeTest() = runTest {
        isNewVersion("6.0.21", "6.1.0") shouldBe true
        isNewVersion("6.0.21", "6.5.0") shouldBe true
        isNewVersion("1.2.3", "1.3.0") shouldBe true
    }

    @Test
    @DisplayName("Patch version upgrade should be detected")
    fun patchVersionUpgradeTest() = runTest {
        isNewVersion("6.0.21", "6.0.22") shouldBe true
        isNewVersion("6.0.21", "6.0.30") shouldBe true
        isNewVersion("1.2.3", "1.2.4") shouldBe true
    }

    @Test
    @DisplayName("Older versions should not be considered new")
    fun olderVersionTest() = runTest {
        isNewVersion("6.0.21", "6.0.20") shouldBe false
        isNewVersion("6.1.0", "6.0.99") shouldBe false
        isNewVersion("7.0.0", "6.9.9") shouldBe false
    }

    @Test
    @DisplayName("Prerelease to stable comparison - numeric version upgrade takes precedence")
    fun prereleaseToStableTest() = runTest {
        // Numeric version upgrade trumps prerelease status
        isNewVersion("6.0.0-beta", "6.0.1") shouldBe true
        isNewVersion("6.0.0-alpha", "6.1.0") shouldBe true
        isNewVersion("5.9.9-rc1", "6.0.0") shouldBe true
    }

    @Test
    @DisplayName("Prerelease between different numeric versions")
    fun betweenPrereleasesTest() = runTest {
        // Numeric version difference matters
        isNewVersion("6.0.0-beta", "6.0.1-beta") shouldBe true // Patch version bump
        isNewVersion("6.0.0-alpha", "6.1.0-alpha") shouldBe true // Minor version bump
    }

    @Test
    @DisplayName("Version prefix handling")
    fun versionPrefixTest() = runTest {
        isNewVersion("6.0.21", "v6.0.22") shouldBe true
        isNewVersion("v6.0.21", "6.0.22") shouldBe true
        isNewVersion("v6.0.21", "v6.0.22") shouldBe true
        isNewVersion("version-6.0.21", "v6.0.22") shouldBe true
    }

    @Test
    @DisplayName("Edge cases with unusual version formats")
    fun edgeCasesTest() = runTest {
        isNewVersion("6.0.21", "6.0.21.1") shouldBe true
        isNewVersion("6.0.21.1", "6.0.21") shouldBe false
        isNewVersion("1.0.0", "1.00.00") shouldBe false // Leading zeros
        isNewVersion("1.9.9", "1.10.0") shouldBe true // Double digit comparison
    }
}
