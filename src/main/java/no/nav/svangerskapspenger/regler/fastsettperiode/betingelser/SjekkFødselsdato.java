package no.nav.svangerskapspenger.regler.fastsettperiode.betingelser;

import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;


@RuleDocumentation(SjekkFødselsdato.ID)
public class SjekkFødselsdato extends LeafSpecification<FastsettePeriodeGrunnlag> {

    public static final String ID = "SVP_VK 14.4.6";

    public SjekkFødselsdato() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
        if (grunnlag.getAvklarteDatoer().getFødselsdato().isPresent()) {
            var startUttaksperiode = grunnlag.getAktuellPeriode().getFom();
            var fødselsdato = grunnlag.getAvklarteDatoer().getFødselsdato().get();
            if (fødselsdato.isBefore(grunnlag.getAvklarteDatoer().getTerminsdato().minusWeeks(3))) {
                if (startUttaksperiode.equals(fødselsdato) || startUttaksperiode.isAfter(fødselsdato)) {
                    return ja();
                }
            }
        }
        return nei();
    }

}
