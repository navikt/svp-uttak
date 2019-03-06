package no.nav.svangerskapspenger.regler.fastsettperiode.betingelser;

import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;

@RuleDocumentation(SjekkFørsteLovligeUttaksdato.ID)
public class SjekkFørsteLovligeUttaksdato extends LeafSpecification<FastsettePeriodeGrunnlag> {

    public static final String ID = "SVP_VK 14.4.5";

    public SjekkFørsteLovligeUttaksdato() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
        var sluttUttaksperiode = grunnlag.getAktuellPeriode().getTom();
        if (sluttUttaksperiode.isBefore(grunnlag.getAvklarteDatoer().getFørsteLovligeUttaksdag())) {
            return ja();
        }
        return nei();
    }

}
