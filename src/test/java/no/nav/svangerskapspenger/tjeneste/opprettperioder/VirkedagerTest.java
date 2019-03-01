package no.nav.svangerskapspenger.tjeneste.opprettperioder;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class VirkedagerTest {
    private Map<DayOfWeek, LocalDate> uke;

    @Before
    public void setUp() {
        LocalDate iDag = LocalDate.now();
        LocalDate mandag = iDag.minusDays(iDag.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
        uke = Stream.of(DayOfWeek.values()).collect(Collectors.toMap(day -> day, day -> mandag.plusDays(day.ordinal())));
    }

    @Test
    public void skalBeregneAntallVirkedager() {
        LocalDate mandag = getDayOfWeek(DayOfWeek.MONDAY);
        LocalDate søndag = getDayOfWeek(DayOfWeek.SUNDAY);

        assertThat(Virkedager.antallVirkedager(mandag, søndag)).isEqualTo(5);
        assertThat(Virkedager.antallVirkedager(mandag, søndag.plusDays(1))).isEqualTo(6);
        assertThat(Virkedager.antallVirkedager(mandag, søndag.plusDays(10))).isEqualTo(13);
        assertThat(Virkedager.antallVirkedager(mandag.plusDays(1), søndag)).isEqualTo(4);
        assertThat(Virkedager.antallVirkedager(mandag.plusDays(1), søndag.plusDays(1))).isEqualTo(5);
        assertThat(Virkedager.antallVirkedager(mandag.plusDays(4), søndag)).isEqualTo(1);
        assertThat(Virkedager.antallVirkedager(mandag.plusDays(5), søndag)).isEqualTo(0);

        assertThat(Virkedager.antallVirkedager(mandag.minusDays(1), søndag)).isEqualTo(5);
        assertThat(Virkedager.antallVirkedager(mandag.minusDays(2), søndag)).isEqualTo(5);
        assertThat(Virkedager.antallVirkedager(mandag.minusDays(3), søndag)).isEqualTo(6);
        assertThat(Virkedager.antallVirkedager(mandag.minusDays(3), søndag.plusDays(1))).isEqualTo(7);
    }


    private LocalDate getDayOfWeek(DayOfWeek dayOfWeek) {
        LocalDate date = uke.get(dayOfWeek);
        assertThat(date.getDayOfWeek()).isEqualTo(dayOfWeek);
        return date;
    }

}
