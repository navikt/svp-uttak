package no.nav.svangerskapspenger.regler.fastsettperiode.betingelser;

import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;

@RuleDocumentation(SjekkBarnetsDødsdato.ID)
public class SjekkBarnetsDødsdato extends LeafSpecification<FastsettePeriodeGrunnlag> {

    public static final String ID = "SVP_VK 14.4.2";

    public SjekkBarnetsDødsdato() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
        return grunnlag.getAvklarteDatoer().getBarnetsDødsdato().map(barnetsDødsdato -> {
            var startUttaksperiode = grunnlag.getAktuellPeriode().getFom();
            return startUttaksperiode.equals(barnetsDødsdato) || startUttaksperiode.isAfter(barnetsDødsdato) ? ja() : nei();
        }).orElse(nei());

    }

}
