package no.nav.svangerskapspenger.domene.søknad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.svangerskapspenger.domene.felles.LukketPeriode;
import no.nav.svangerskapspenger.utils.BevegeligeHelligdagerUtil;

public class Ferie extends LukketPeriode {

    private Ferie(LocalDate fom, LocalDate tom) {
        super(fom, tom);
    }

    public static List<Ferie> opprett(LocalDate fom, LocalDate tom) {
        var feriePerioder = new ArrayList<Ferie>();
        var rest = new Ferie(fom, tom);
        var helligdager = BevegeligeHelligdagerUtil.finnBevegeligeHelligdagerUtenHelg(rest);
        for (LocalDate helligdag : helligdager) {
            if (rest.overlapper(helligdag)) {
                var minstToFeriedager = rest.getFom().isBefore(rest.getTom());
                if (!minstToFeriedager) {
                    //Bare en dag, og det er helligdag.
                    return Collections.emptyList();
                } else if (helligdag.equals(rest.getFom())) {
                    //Helligdag på første feriedag, kutt en dag i starten av ferie.
                    rest = new Ferie(rest.getFom().plusDays(1), rest.getTom());
                } else if (helligdag.equals(rest.getTom())) {
                    //Helligdag på siste dag i ferien, kutt en dag i slutten av ferien. Resten av helligdagene må være etter ferien og avbryter derfor loopen.
                    rest = new Ferie(rest.getFom(), rest.getTom().minusDays(1));
                    break;
                } else {
                    feriePerioder.add(new Ferie(rest.getFom(), helligdag.minusDays(1)));
                    rest = new Ferie (helligdag.plusDays(1), rest.getTom());
                }
            }
        }
        feriePerioder.add(rest);
        return feriePerioder;
    }

}
