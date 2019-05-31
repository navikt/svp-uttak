package no.nav.svangerskapspenger.tjeneste.opprettperioder;

import no.nav.svangerskapspenger.domene.felles.Arbeidsforhold;
import no.nav.svangerskapspenger.domene.resultat.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperiode;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;
import no.nav.svangerskapspenger.domene.søknad.Søknad;
import no.nav.svangerskapspenger.domene.søknad.Tilrettelegging;
import no.nav.svangerskapspenger.domene.søknad.TilretteleggingKryss;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UttaksperioderTjenesteV2 implements UttaksperioderTjeneste {

    private static final BigDecimal FULL_UTBETALINGSGRAD = BigDecimal.valueOf(100L);

    @Override
    public Set<ManuellBehandling> opprett(List<Søknad> søknader, Uttaksperioder uttaksperioder) {
        var manuellbehandlingSet = EnumSet.noneOf(ManuellBehandling.class);


        søknader.forEach(søknad -> {

            if (søknad.getTilretteliggingBehovDato().isAfter(søknad.sisteDagFørTermin())) {
                uttaksperioder.avslåForArbeidsforhold(søknad.getArbeidsforhold(), ArbeidsforholdIkkeOppfyltÅrsak.LEGES_DATO_IKKE_FØR_TRE_UKER_FØR_TERMINDATO);
            } else {
                var tilrettelegginger = fjernUnødvendigeTilrettelegginger(søknad);


                avklarPerioder(søknad, tilrettelegginger, uttaksperioder);

            }
        });

        return manuellbehandlingSet;
    }

    private void avklarPerioder(Søknad søknad, List<Tilrettelegging> tilrettelegginger, Uttaksperioder uttaksperioder) {
        var aTilrettelegginger = tilrettelegginger.stream().filter(t -> t.getTilretteleggingKryss().equals(TilretteleggingKryss.A)).collect(Collectors.toList());

        if (aTilrettelegginger.size() == 1 && aTilrettelegginger.get(0).getArbeidsgiversDato().equals(søknad.getTilretteliggingBehovDato())) {
            uttaksperioder.avslåForArbeidsforhold(søknad.getArbeidsforhold(), ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE);
            return;
        }

        var cTilrettelegginger = tilrettelegginger.stream().filter(t -> t.getTilretteleggingKryss().equals(TilretteleggingKryss.C)).collect(Collectors.toList());

        if (cTilrettelegginger.size() == 1 && cTilrettelegginger.get(0).getArbeidsgiversDato().isAfter(søknad.sisteDagFørTermin())) {
            uttaksperioder.avslåForArbeidsforhold(søknad.getArbeidsforhold(), ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN);
            return;
        }

        if (tilrettelegginger.isEmpty()) {
            uttaksperioder.leggTilPerioder(søknad.getArbeidsforhold(), new Uttaksperiode(søknad.getTilretteliggingBehovDato(), søknad.sisteDagFørTermin(), FULL_UTBETALINGSGRAD));
            return;
        }
        if (cTilrettelegginger.size() == 1 && cTilrettelegginger.get(0).getArbeidsgiversDato().equals(søknad.getTilretteliggingBehovDato())) {
            uttaksperioder.leggTilPerioder(søknad.getArbeidsforhold(), new Uttaksperiode(søknad.getTilretteliggingBehovDato(), søknad.sisteDagFørTermin(), FULL_UTBETALINGSGRAD));
            return;
        }


        var sorterteTilrettelegginger = tilrettelegginger.stream().sorted(Comparator.comparing(Tilrettelegging::getArbeidsgiversDato)).collect(Collectors.toList());
        LocalDate fom;
        LocalDate tom;
        LocalDate nesteFom = søknad.getTilretteliggingBehovDato();
        for (int i = 0; i < sorterteTilrettelegginger.size(); i++) {
            fom = nesteFom;
            tom = tilrettelegginger.get(i).getArbeidsgiversDato().minusDays(1);
            var utbetalingsgrad = FULL_UTBETALINGSGRAD;
            if (i > 0) {
                var kryss = tilrettelegginger.get(i-1);

                if (kryss.equals(TilretteleggingKryss.A)) {
                    utbetalingsgrad = BigDecimal.ZERO;
                } else if (kryss.equals(TilretteleggingKryss.B)) {
                    //TODO ta hensyn til stillingsprosent fra  aareg
                    utbetalingsgrad = FULL_UTBETALINGSGRAD.subtract(kryss.getTilretteleggingsprosent().divide(BigDecimal.valueOf(100L)).multiply(BigDecimal.valueOf(100L)));
                }
            }
            opprettPeriode(uttaksperioder, søknad.getArbeidsforhold(), fom, tom, utbetalingsgrad);
            nesteFom = tom.plusDays(1);
        }
        opprettPeriode(uttaksperioder, søknad.getArbeidsforhold(), nesteFom, søknad.sisteDagFørTermin(), FULL_UTBETALINGSGRAD);
    }

    private void opprettPeriode(Uttaksperioder uttaksperioder, Arbeidsforhold arbeidsforhold, LocalDate fom, LocalDate tom, BigDecimal utbetalingsgrad) {
        if (tom.isAfter(fom) || fom.equals(tom)) {
            uttaksperioder.leggTilPerioder(arbeidsforhold, new Uttaksperiode(fom, tom, utbetalingsgrad));
        }
    }

    private List<Tilrettelegging> fjernUnødvendigeTilrettelegginger(Søknad søknad) {
        var tilretteleggingerEtterBehovDato = søknad.getTilrettelegginger()
            .stream()
            .filter(tilrettelegging ->
                tilrettelegging.getArbeidsgiversDato().isAfter(søknad.getTilretteliggingBehovDato()))
            .collect(Collectors.toList());

        var tilretteleggingerFørBehovDato = søknad.getTilrettelegginger()
            .stream()
            .filter(tilrettelegging ->
                tilrettelegging.getArbeidsgiversDato().isBefore(søknad.getTilretteliggingBehovDato()))
            .collect(Collectors.toList());

        var tilretteleggingerPåBehovDato = søknad.getTilrettelegginger()
            .stream()
            .filter(tilrettelegging ->
                tilrettelegging.getArbeidsgiversDato().equals(søknad.getTilretteliggingBehovDato()))
            .collect(Collectors.toList());

        var resultat = tilretteleggingerEtterBehovDato;


        if (!tilretteleggingerFørBehovDato.isEmpty()) {

            if (!tilretteleggingerPåBehovDato.isEmpty()) {
                join(resultat, tilretteleggingerPåBehovDato);
            } else {
                var sisteTilrettelegging = sisteArbeidsgiverDato(tilretteleggingerEtterBehovDato);
                join(resultat, List.of(sisteTilrettelegging));
            }

        }

        return resultat
            .stream()
            .filter(tilrettelegging -> !tilrettelegging.getArbeidsgiversDato().isAfter(søknad.sisteDagFørTermin()) && !tilrettelegging.getTilretteleggingKryss().equals(TilretteleggingKryss.C))
            .collect(Collectors.toList());
    }



    private List<Tilrettelegging> join(List<Tilrettelegging> t1, List<Tilrettelegging> t2) {
        var samletListe = new ArrayList<Tilrettelegging>();
        samletListe.addAll(t1);
        samletListe.addAll(t2);
        return samletListe;
    }

    private Tilrettelegging sisteArbeidsgiverDato(List<Tilrettelegging> tilrettelegginger) {
        var sortert = tilrettelegginger.stream().sorted(Comparator.comparing(Tilrettelegging::getArbeidsgiversDato)).collect(Collectors.toList());
        return sortert.get(sortert.size()-1);
    }

}
