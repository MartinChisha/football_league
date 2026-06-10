package com.backspacestudios.league_management.competition.util;

import com.backspacestudios.league_management.competition.entity.Fixture;
import com.backspacestudios.league_management.competition.enums.FixtureStatus;
import com.backspacestudios.league_management.team.entity.Team;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class RoundRobinScheduler {

    public List<Fixture> generateFixtures(UUID seasonId, List<Team> teams, int legs, Long randomSeed) {
        List<Team> shuffled = new ArrayList<>(teams);
        if (randomSeed != null) {
            Collections.shuffle(shuffled, new Random(randomSeed));
        } else {
            shuffled.sort(Comparator.comparing(Team::getTeamId));
        }

        boolean hasDummy = shuffled.size() % 2 != 0;
        if (hasDummy) shuffled.add(null);

        int totalTeams = shuffled.size();
        int roundsPerLeg = totalTeams - 1;
        List<Fixture> allFixtures = new ArrayList<>();

        for (int leg = 0; leg < legs; leg++) {
            List<Team> currentOrder = new ArrayList<>(shuffled);
            for (int round = 0; round < roundsPerLeg; round++) {
                int week = leg * roundsPerLeg + round + 1;
                for (int i = 0; i < totalTeams / 2; i++) {
                    Team home = currentOrder.get(i);
                    Team away = currentOrder.get(totalTeams - 1 - i);
                    if (home != null && away != null) {
                        if (leg == 1) {
                            allFixtures.add(createFixture(seasonId, week, away, home));
                        } else {
                            allFixtures.add(createFixture(seasonId, week, home, away));
                        }
                    }
                }
                // rotate (circle method)
                Collections.rotate(currentOrder.subList(1, totalTeams), 1);
            }
        }
        return allFixtures;
    }

    private Fixture createFixture(UUID seasonId, int week, Team home, Team away) {
        Fixture f = new Fixture();
        f.setSeasonId(seasonId);
        f.setMatchWeek(week);
        f.setHomeTeamId(home.getTeamId());
        f.setAwayTeamId(away.getTeamId());
        f.setStatus(FixtureStatus.SCHEDULED);
        return f;
    }
}