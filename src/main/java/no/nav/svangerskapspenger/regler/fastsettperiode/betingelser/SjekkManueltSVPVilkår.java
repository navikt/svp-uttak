package no.nav.svangerskapspenger.regler.fastsettperiode.betingelser;

import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.svangerskapspenger.regler.fastsettperiode.grunnlag.FastsettePeriodeGrunnlag;

@RuleDocumentation(SjekkManueltSVPVilkår.ID)
public class SjekkManueltSVPVilkår extends LeafSpecification<FastsettePeriodeGrunnlag> {
    public static final String ID ="SVP_VK 14.4.";

    public SjekkManueltSVPVilkår() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(FastsettePeriodeGrunnlag grunnlag) {
        return !grunnlag.getInngangsvilkår().manueltSVPVilkårOppfylt() ? ja() : nei();
        }
}
