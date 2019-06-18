package no.nav.svangerskapspenger.regler.fastsettperiode.betingelser;

import java.math.BigDecimal;

import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;

@RuleDocumentation(SjekkHullUttak.ID)
public class SjekkHullUttak extends LeafSpecification<FastsettePeriodeGrunnlag> {

    public static final String ID = "SVP_VK_14.4.9";

    public SjekkHullUttak() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
        var eventuellStartHullUttak = grunnlag.getAvklarteDatoer().getStartOppholdUttak();
        if (eventuellStartHullUttak.isPresent()) {
            if (grunnlag.getAktuellPeriode().getFom().isBefore(eventuellStartHullUttak.get())
                || BigDecimal.ZERO.equals(grunnlag.getAktuellPeriode().getUtbetalingsgrad())) {
                return nei();
            }
            return ja();
        }
        return nei();
    }

}
