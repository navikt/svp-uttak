package no.nav.svangerskapspenger.domene.søknad;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.Test;

public class FerieTest {

    @Test
    public void ferie_uten_røde_dager_skal_ikke_føre_til_knekk() {
        var fom = LocalDate.of(2019, 5, 13);
        var tom = LocalDate.of(2019, 5, 16);
        var ferieListe = Ferie.opprett(fom, tom);

        assertThat(ferieListe).hasSize(1);
        assertThat(ferieListe.get(0).getFom()).isEqualTo(fom);
        assertThat(ferieListe.get(0).getTom()).isEqualTo(tom);
    }

    @Test
    public void rød_dag_i_begynnelsen_fører_til_avkorting_i_begynnelsen() {
        var fom = LocalDate.of(2019, 5, 1);
        var tom = LocalDate.of(2019, 5, 3);

        var ferieListe = Ferie.opprett(fom, tom);

        assertThat(ferieListe).hasSize(1);
        assertThat(ferieListe.get(0).getFom()).isEqualTo(fom.plusDays(1));
        assertThat(ferieListe.get(0).getTom()).isEqualTo(tom);
    }

    @Test
    public void rød_dag_i_slutten_fører_til_avkorting_i_slutten() {
        var fom = LocalDate.of(2019, 5, 13);
        var tom = LocalDate.of(2019, 5, 17);

        var ferieListe = Ferie.opprett(fom, tom);

        assertThat(ferieListe).hasSize(1);
        assertThat(ferieListe.get(0).getFom()).isEqualTo(fom);
        assertThat(ferieListe.get(0).getTom()).isEqualTo(tom.minusDays(1));
    }


    @Test
    public void rød_dag_i_begynnelsen_og_slutten_fører_til_avkorting_i_begynnelsen_og_slutten() {
        var fom = LocalDate.of(2019, 5, 1);
        var tom = LocalDate.of(2019, 5, 17);

        var ferieListe = Ferie.opprett(fom, tom);

        assertThat(ferieListe).hasSize(1);
        assertThat(ferieListe.get(0).getFom()).isEqualTo(fom.plusDays(1));
        assertThat(ferieListe.get(0).getTom()).isEqualTo(tom.minusDays(1));
    }

    @Test
    public void rød_dag_midt_i_periode_fører_til_2_perioder() {
            var fom = LocalDate.of(2019, 5, 2);
            var tom = LocalDate.of(2019, 5, 19);

            var ferieListe = Ferie.opprett(fom, tom);

            assertThat(ferieListe).hasSize(2);
            assertThat(ferieListe.get(0).getFom()).isEqualTo(fom);
            assertThat(ferieListe.get(0).getTom()).isEqualTo(LocalDate.of(2019, 5, 16));
            assertThat(ferieListe.get(1).getFom()).isEqualTo(LocalDate.of(2019, 5, 18));
            assertThat(ferieListe.get(1).getTom()).isEqualTo(tom);
    }

    @Test
    public void to_røde_dager_midt_i_periode_fører_til_3_perioder() {
        var fom = LocalDate.of(2019, 4, 29);
        var tom = LocalDate.of(2019, 5, 19);

        var ferieListe = Ferie.opprett(fom, tom);

        assertThat(ferieListe).hasSize(3);
        assertThat(ferieListe.get(0).getFom()).isEqualTo(fom);
        assertThat(ferieListe.get(0).getTom()).isEqualTo(LocalDate.of(2019, 4, 30));
        assertThat(ferieListe.get(1).getFom()).isEqualTo(LocalDate.of(2019, 5, 2));
        assertThat(ferieListe.get(1).getTom()).isEqualTo(LocalDate.of(2019, 5, 16));
        assertThat(ferieListe.get(2).getFom()).isEqualTo(LocalDate.of(2019, 5, 18));
        assertThat(ferieListe.get(2).getTom()).isEqualTo(tom);
    }

    @Test
    public void ferie_på_en_dag_uten_overlapp_med_helligdag_fører_til_uendret_periode() {
        var fom = LocalDate.of(2019, 5, 2);
        var tom = LocalDate.of(2019, 5, 2);

        var ferieListe = Ferie.opprett(fom , tom);

        assertThat(ferieListe).hasSize(1);
        assertThat(ferieListe.get(0).getFom()).isEqualTo(fom);
        assertThat(ferieListe.get(0).getTom()).isEqualTo(tom);
    }

    @Test
    public void ferie_på_en_dag_med_overlapp_med_helligdag_fører_til_ingen_periode() {
        var fom = LocalDate.of(2019, 5, 1);
        var tom = LocalDate.of(2019, 5, 1);

        var ferieListe = Ferie.opprett(fom, tom);

        assertThat(ferieListe).isEmpty();
    }

    @Test
    public void overlapp_i_begynnelse_og_midten_fører_til_2_perioder() {
        var fom = LocalDate.of(2019, 5, 1);
        var tom = LocalDate.of(2019, 5, 19);

        var ferieListe = Ferie.opprett(fom, tom);

        assertThat(ferieListe).hasSize(2);
        assertThat(ferieListe.get(0).getFom()).isEqualTo(fom.plusDays(1));
        assertThat(ferieListe.get(0).getTom()).isEqualTo(LocalDate.of(2019, 5, 16));
        assertThat(ferieListe.get(1).getFom()).isEqualTo(LocalDate.of(2019, 5, 18));
        assertThat(ferieListe.get(1).getTom()).isEqualTo(tom);
    }

    @Test
    public void overlapp_på_slutten_og_midten_fører_til_2_perioder() {
        var fom = LocalDate.of(2019, 4, 29);
        var tom = LocalDate.of(2019, 5, 16);

        var ferieListe = Ferie.opprett(fom, tom);

        assertThat(ferieListe).hasSize(2);
        assertThat(ferieListe.get(0).getFom()).isEqualTo(fom);
        assertThat(ferieListe.get(0).getTom()).isEqualTo(LocalDate.of(2019, 4, 30));
        assertThat(ferieListe.get(1).getFom()).isEqualTo(LocalDate.of(2019, 5, 2));
        assertThat(ferieListe.get(1).getTom()).isEqualTo(tom);
    }

}
