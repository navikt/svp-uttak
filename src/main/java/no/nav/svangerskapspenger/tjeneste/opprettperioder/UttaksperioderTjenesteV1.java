package no.nav.svangerskapspenger.tjeneste.opprettperioder;

import no.nav.svangerskapspenger.domene.resultat.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;
import no.nav.svangerskapspenger.domene.søknad.Søknad;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

//TODO SVP slettes når vi har verifisert at v2 virker.
public class UttaksperioderTjenesteV1 implements UttaksperioderTjeneste {

    @Override
    public Set<ManuellBehandling> opprett(List<Søknad> søknader, Uttaksperioder uttaksperioder) {
        var manuellbehandlingSet = EnumSet.noneOf(ManuellBehandling.class);

        søknader.forEach(søknad -> {
            if (søknad.getTilretteliggingBehovDato().isAfter(søknad.getTermindato().minusWeeks(3).minusDays(1))) {
                uttaksperioder.avslåForArbeidsforhold(søknad.getArbeidsforhold(),
                    ArbeidsforholdIkkeOppfyltÅrsak.LEGES_DATO_IKKE_FØR_TRE_UKER_FØR_TERMINDATO);
            } else {
                if (søknad.getTilrettelegginger().size() != 1) {
                    manuellbehandlingSet.add(ManuellBehandling.AVKLAR_TILRETTELIGGING);
                }
                var tilrettelegging = søknad.getTilrettelegginger().get(0);  //TODO legg til støtte for flere kryss
                tilrettelegging.opprettPerioder(uttaksperioder, søknad);
            }

        });

        return manuellbehandlingSet;
    }

}
