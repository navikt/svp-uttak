package no.nav.svangerskapspenger.tjeneste.fastsettuttak;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import no.nav.svangerskapspenger.domene.søknad.Ferie;
import org.junit.Test;

import no.nav.svangerskapspenger.domene.felles.AktivitetType;
import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;

public class UttakHullUtlederTest {

    @Test
    public void enkelt_uttak_med_et_arbeidsforhold_uten_hull() {
        var arb1 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "1", null);

        var perioder = new Uttaksperioder();
        perioder.leggTilPerioder(arb1,
            new Uttaksperiode(LocalDate.of(2019, 7, 1), LocalDate.of(2019, 7, 7), BigDecimal.valueOf(100)),
            new Uttaksperiode(LocalDate.of(2019, 7, 8), LocalDate.of(2019, 8, 4), BigDecimal.valueOf(75)),
            new Uttaksperiode(LocalDate.of(2019, 8, 5), LocalDate.of(2019, 9, 10), BigDecimal.valueOf(100))
        );

        var førsteHull = new UttakHullUtleder().finnStartHull(perioder, List.of());

        assertThat(førsteHull).isEqualTo(Optional.empty());
    }

    @Test
    public void enkelt_uttak_med_et_arbeidsforhold_med_hull() {
        var arb1 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "1", null);

        var perioder = new Uttaksperioder();
        perioder.leggTilPerioder(arb1,
            new Uttaksperiode(LocalDate.of(2019, 7, 1), LocalDate.of(2019, 7, 7), BigDecimal.valueOf(100)),
            new Uttaksperiode(LocalDate.of(2019, 7, 8), LocalDate.of(2019, 8, 4), BigDecimal.valueOf(0)),
            new Uttaksperiode(LocalDate.of(2019, 8, 5), LocalDate.of(2019, 9, 10), BigDecimal.valueOf(100))
        );

        var førsteHull = new UttakHullUtleder().finnStartHull(perioder, List.of());

        assertThat(førsteHull).isEqualTo(Optional.of(LocalDate.of(2019,7,8)));
    }

    @Test
    public void komplekst_uttak_med_flere_arbeidsforhold_uten_hull() {

        var arb1 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "1", null);
        var arb2 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "2", null);
        var arb3 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "3", null);
        var arb4 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "4", null);
        var arb5 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "5", null);

        var perioder = new Uttaksperioder();
        perioder.leggTilPerioder(arb1,
            new Uttaksperiode(LocalDate.of(2019, 7, 1), LocalDate.of(2019, 7, 7), BigDecimal.valueOf(100)),
            new Uttaksperiode(LocalDate.of(2019, 7, 8), LocalDate.of(2019, 8, 4), BigDecimal.valueOf(75)),
            new Uttaksperiode(LocalDate.of(2019, 8, 5), LocalDate.of(2019, 9, 10), BigDecimal.valueOf(100)),
            new Uttaksperiode(LocalDate.of(2019, 9, 11), LocalDate.of(2019, 10, 8), BigDecimal.valueOf(0))
        );

        perioder.leggTilPerioder(arb2,
            new Uttaksperiode(LocalDate.of(2019, 7, 1), LocalDate.of(2019, 7, 10), BigDecimal.valueOf(100)),
            new Uttaksperiode(LocalDate.of(2019, 7, 11), LocalDate.of(2019, 7, 30), BigDecimal.valueOf(0)),
            new Uttaksperiode(LocalDate.of(2019, 7, 31), LocalDate.of(2019, 8, 20), BigDecimal.valueOf(50)),
            new Uttaksperiode(LocalDate.of(2019, 8, 21), LocalDate.of(2019, 10, 8), BigDecimal.valueOf(100))
        );

        perioder.leggTilPerioder(arb3,
            new Uttaksperiode(LocalDate.of(2019, 7, 1), LocalDate.of(2019, 7, 23), BigDecimal.valueOf(100)),
            new Uttaksperiode(LocalDate.of(2019, 7, 24), LocalDate.of(2019, 8, 5), BigDecimal.valueOf(0)),
            new Uttaksperiode(LocalDate.of(2019, 8, 6), LocalDate.of(2019, 10, 8), BigDecimal.valueOf(75))
        );

        perioder.leggTilPerioder(arb4,
            new Uttaksperiode(LocalDate.of(2019, 7, 1), LocalDate.of(2019, 7, 26), BigDecimal.valueOf(100)),
            new Uttaksperiode(LocalDate.of(2019, 7, 25), LocalDate.of(2019, 8, 30), BigDecimal.valueOf(76.67)),
            new Uttaksperiode(LocalDate.of(2019, 8, 31), LocalDate.of(2019, 9, 3), BigDecimal.valueOf(100)),
            new Uttaksperiode(LocalDate.of(2019, 9, 4), LocalDate.of(2019, 10, 8), BigDecimal.valueOf(0))
        );

        perioder.leggTilPerioder(arb5,
            new Uttaksperiode(LocalDate.of(2019, 7, 1), LocalDate.of(2019, 7, 24), BigDecimal.valueOf(80)),
            new Uttaksperiode(LocalDate.of(2019, 7, 25), LocalDate.of(2019, 9, 9), BigDecimal.valueOf(0)),
            new Uttaksperiode(LocalDate.of(2019, 9, 10), LocalDate.of(2019, 10, 8), BigDecimal.valueOf(100))
        );


        var førsteHull = new UttakHullUtleder().finnStartHull(perioder, List.of());

        assertThat(førsteHull).isEqualTo(Optional.empty());
    }

    @Test
    public void overlappende_ferie_midt_i_periode_med_0_utbetaling_skal_ikke_gi_hull() {
        var arb1 = Arbeidsforhold.virksomhet(AktivitetType.ARBEID, "1", null);

        var perioder = new Uttaksperioder();
        perioder.leggTilPerioder(arb1,
            new Uttaksperiode(LocalDate.of(2019, 7, 8), LocalDate.of(2019, 8, 4), BigDecimal.valueOf(0)),
            new Uttaksperiode(LocalDate.of(2019, 8, 5), LocalDate.of(2019, 9, 10), BigDecimal.valueOf(100))
        );

        var førsteHull = new UttakHullUtleder().finnStartHull(perioder, Ferie.opprett(LocalDate.of(2019, 7, 20), LocalDate.of(2019, 7, 27)));

        assertThat(førsteHull).isEqualTo(Optional.empty());
    }

}
