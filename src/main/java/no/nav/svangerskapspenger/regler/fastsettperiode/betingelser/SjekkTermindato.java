package no.nav.svangerskapspenger.regler.fastsettperiode.betingelser;

import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;

@RuleDocumentation(SjekkTermindato.ID)
public class SjekkTermindato extends LeafSpecification<FastsettePeriodeGrunnlag> {

    public static final String ID = "FP_VK x.x";

    public SjekkTermindato() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
            var startUttaksperiode = grunnlag.getAktuellPeriode().getFom();
            var sisteLovligeUttaksdag = grunnlag.getAvklarteDatoer().getTerminsdato().minusWeeks(3);
            if (startUttaksperiode.equals(sisteLovligeUttaksdag) || startUttaksperiode.isAfter(sisteLovligeUttaksdag)) {
                return ja();
            }
        return nei();
    }

}
