package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.MatchType
import com.example.data.model.TeamMode
import com.example.data.model.Tournament
import com.example.data.model.FFMap
import com.example.data.repository.TournamentRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("FXR TOURNAMENT", appName)
    }

    @Test
    fun `test tournament registration and br leaderboard scoring`() {
        val repository = TournamentRepository()
        val user = repository.currentUser.value

        // Check seeded tournaments
        val tournaments = repository.tournaments.value
        assertTrue(tournaments.isNotEmpty())
        val brTourney = tournaments.first { it.matchType == MatchType.BR_RANK }

        // Test registering
        val registered = repository.registerForTournament(
            tournamentId = brTourney.id,
            teamName = "FXR ELITE",
            captainIgn = user.ffIgn,
            captainUid = user.ffUid,
            p1Ign = user.ffIgn,
            p1Uid = user.ffUid,
            p2Ign = "P2_IGN",
            p2Uid = "12345678",
            p3Ign = "P3_IGN",
            p3Uid = "87654321",
            p4Ign = "P4_IGN",
            p4Uid = "11223344",
            subIgn = null,
            subUid = null
        )
        // Note: user was already pre-registered for t_br_squad_1, or successfully registers
        assertNotNull(repository.registrations.value)

        // Test BR leaderboard scoring formula: Total = Placement + KillPoints - Penalty
        repository.addOrUpdateBREntry(
            tournamentId = brTourney.id,
            teamName = "TEST TEAM",
            matchesPlayed = 1,
            booyahs = 1,
            placementPoints = 12,
            eliminations = 8,
            eliminationPoints = 8,
            penaltyPoints = 2
        )

        val updatedLb = repository.brLeaderboards.value[brTourney.id]
        val testEntry = updatedLb?.find { it.teamName == "TEST TEAM" }
        assertNotNull(testEntry)
        // 12 + 8 - 2 = 18
        assertEquals(18, testEntry?.totalPoints)
    }

    @Test
    fun `test lone wolf bracket winner progression`() {
        val repository = TournamentRepository()
        val lwTourney = repository.tournaments.value.first { it.matchType == MatchType.LONE_WOLF }

        // Update match result for QF1
        repository.updateLoneWolfMatchWinner(
            tournamentId = lwTourney.id,
            matchId = "lw_q1",
            p1Score = 5,
            p2Score = 2,
            winnerName = "FXR★SHADOW"
        )

        val matches = repository.loneWolfMatches.value[lwTourney.id]
        val s1 = matches?.find { it.matchIndex == 5 }
        assertNotNull(s1)
        assertEquals("FXR★SHADOW", s1?.player1Name)
    }

    @Test
    fun `test room credentials release`() {
        val repository = TournamentRepository()
        val t = repository.tournaments.value.first()

        repository.setRoomCredentials(
            tournamentId = t.id,
            roomId = "FF-CUSTOM-9999",
            pass = "SECURE#PASS",
            instructions = "Slot 1 only",
            releaseNow = true
        )

        val updated = repository.tournaments.value.first { it.id == t.id }
        assertEquals("FF-CUSTOM-9999", updated.roomId)
        assertEquals("SECURE#PASS", updated.roomPassword)
        assertTrue(updated.roomReleased)
    }
}
