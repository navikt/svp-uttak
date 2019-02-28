package no.nav.svangerskapspenger.orkestrering.opprettperioder;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import no.nav.svangerskapspenger.domene.søknad.Søknad;
import no.nav.svangerskapspenger.domene.resultat.Uttaksperioder;

public class UttaksperioderTjeneste {

    public Set<ManuellBehandling> opprett(List<Søknad> søknader, Uttaksperioder uttaksperioder) {
        var manuellbehandlingSet = EnumSet.noneOf(ManuellBehandling.class);

        søknader.forEach(søknad -> {
            if (søknad.getTilrettelegginger().size() != 1) {
                manuellbehandlingSet.add(ManuellBehandling.AVKLAR_TILRETTELIGGING);
            }
            var tilrettelegging = søknad.getTilrettelegginger().get(0);
            tilrettelegging.opprettPerioder(uttaksperioder, søknad);
        });

        return manuellbehandlingSet;
    }

}
