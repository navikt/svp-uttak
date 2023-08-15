package no.nav.svangerskapspenger.domene.søknad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.svangerskapspenger.domene.felles.LukketPeriode;
import no.nav.svangerskapspenger.tjeneste.fastsettuttak.SvpOppholdÅrsak;
import no.nav.svangerskapspenger.utils.BevegeligeHelligdagerUtil;

public class Opphold extends LukketPeriode {
    private SvpOppholdÅrsak årsak;

    private Opphold(LocalDate fom, LocalDate tom, SvpOppholdÅrsak årsak) {
        super(fom, tom);
        this.årsak = årsak;
    }

    public SvpOppholdÅrsak getÅrsak() {
        return årsak;
    }

    public static List<Opphold> opprett(LocalDate fom, LocalDate tom, SvpOppholdÅrsak årsak) {
        var oppholdPerioder = new ArrayList<Opphold>();
        var opppholdPeriode = new Opphold(fom, tom, årsak);

        //Når det er opphold pga annen ytelse (sykepenger)skal vi ikke ta hensyn til bevegelige helligdager
        if (SvpOppholdÅrsak.FERIE.equals(årsak)) {
            var helligdager = BevegeligeHelligdagerUtil.finnBevegeligeHelligdagerUtenHelg(opppholdPeriode);
            for (LocalDate helligdag : helligdager) {
                if (opppholdPeriode.overlapper(helligdag)) {
                    var minstToOppholdDager = opppholdPeriode.getFom().isBefore(opppholdPeriode.getTom());
                    if (!minstToOppholdDager) {
                        //Bare en dag, og det er helligdag.
                        return Collections.emptyList();
                    } else if (helligdag.equals(opppholdPeriode.getFom())) {
                        //Helligdag på første dag i oppholdet, kutt en dag i starten av oppholdet.
                        opppholdPeriode = new Opphold(opppholdPeriode.getFom().plusDays(1), opppholdPeriode.getTom(), årsak);
                    } else if (helligdag.equals(opppholdPeriode.getTom())) {
                        //Helligdag på siste dag i oppholdet, kutt en dag i slutten av ophholdet. Resten av helligdagene må være etter oppholdet, og avbryter derfor loopen.
                        opppholdPeriode = new Opphold(opppholdPeriode.getFom(), opppholdPeriode.getTom().minusDays(1), årsak);
                        break;
                    } else {
                        oppholdPerioder.add(new Opphold(opppholdPeriode.getFom(), helligdag.minusDays(1), årsak));
                        opppholdPeriode = new Opphold(helligdag.plusDays(1), opppholdPeriode.getTom(), årsak);
                    }
                }
            }
        }
        oppholdPerioder.add(opppholdPeriode);
        return oppholdPerioder;
    }

}
