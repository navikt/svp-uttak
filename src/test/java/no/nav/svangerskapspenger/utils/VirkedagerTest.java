package no.nav.svangerskapspenger.utils;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class VirkedagerTest {
    private Map<DayOfWeek, LocalDate> uke;

    @BeforeEach
    void setUp() {
        LocalDate iDag = LocalDate.now();
        LocalDate mandag = iDag.minusDays(iDag.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
        uke = Stream.of(DayOfWeek.values()).collect(Collectors.toMap(day -> day, day -> mandag.plusDays(day.ordinal())));
    }

    @Test
    void skalBeregneAntallVirkedager() {
        LocalDate mandag = getDayOfWeek(DayOfWeek.MONDAY);
        LocalDate søndag = getDayOfWeek(DayOfWeek.SUNDAY);

        assertThat(Virkedager.antallVirkedager(mandag, søndag)).isEqualTo(5);
        assertThat(Virkedager.antallVirkedager(mandag, søndag.plusDays(1))).isEqualTo(6);
        assertThat(Virkedager.antallVirkedager(mandag, søndag.plusDays(10))).isEqualTo(13);
        assertThat(Virkedager.antallVirkedager(mandag.plusDays(1), søndag)).isEqualTo(4);
        assertThat(Virkedager.antallVirkedager(mandag.plusDays(1), søndag.plusDays(1))).isEqualTo(5);
        assertThat(Virkedager.antallVirkedager(mandag.plusDays(4), søndag)).isEqualTo(1);
        assertThat(Virkedager.antallVirkedager(mandag.plusDays(5), søndag)).isZero();

        assertThat(Virkedager.antallVirkedager(mandag.minusDays(1), søndag)).isEqualTo(5);
        assertThat(Virkedager.antallVirkedager(mandag.minusDays(2), søndag)).isEqualTo(5);
        assertThat(Virkedager.antallVirkedager(mandag.minusDays(3), søndag)).isEqualTo(6);
        assertThat(Virkedager.antallVirkedager(mandag.minusDays(3), søndag.plusDays(1))).isEqualTo(7);
    }

    @Test
    void skal_kaste_exception_dersom_datoene_har_feil_rekkefølge() {
        var mandag = LocalDate.of(2019, Month.MARCH, 4);
        var tirsdag = LocalDate.of(2019, Month.MARCH, 5);
        assertThrows(IllegalArgumentException.class, () -> Virkedager.antallVirkedager(tirsdag, mandag));
    }

    @Test
    void skal_kaste_exception_dersom_perioden_er_for_lang() {
        assertThrows(UnsupportedOperationException.class, () -> Virkedager.antallVirkedager(LocalDate.of(-100000000, 1, 1), LocalDate.of(20000000,1, 1)));
    }

    private LocalDate getDayOfWeek(DayOfWeek dayOfWeek) {
        LocalDate date = uke.get(dayOfWeek);
        assertThat(date.getDayOfWeek()).isEqualTo(dayOfWeek);
        return date;
    }

}
