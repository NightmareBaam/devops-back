package com.devops.back.rest.groupe;

import com.devops.back.domain.groupe.casusage.GererGroupes;
import com.devops.back.domain.groupe.casusage.RechercherRepositories;
import com.devops.back.domain.groupe.casusage.RecupererDetailRepository;
import com.devops.back.rest.commun.model.RepositoryBitbucketRest;
import com.devops.back.rest.groupe.model.DemandeGroupeRest;
import com.devops.back.rest.groupe.model.DetailRepositoryRest;
import com.devops.back.rest.groupe.model.GroupeDetailRest;
import com.devops.back.rest.groupe.model.GroupeResumeRest;
import com.devops.back.rest.groupe.model.VersionEnvironnementRest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groupes")
public class GroupeController {

    private final GererGroupes gererGroupes;
    private final RechercherRepositories rechercherRepositories;
    private final RecupererDetailRepository recupererDetailRepository;

    public GroupeController(
            GererGroupes gererGroupes,
            RechercherRepositories rechercherRepositories,
            RecupererDetailRepository recupererDetailRepository
    ) {
        this.gererGroupes = gererGroupes;
        this.rechercherRepositories = rechercherRepositories;
        this.recupererDetailRepository = recupererDetailRepository;
    }

    @GetMapping
    public List<GroupeResumeRest> rechercher(
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return gererGroupes.rechercher(start, limit).stream()
                .map(GroupeResumeRest::depuisDomaine)
                .toList();
    }

    @GetMapping("/{id}")
    public GroupeDetailRest recuperer(@PathVariable UUID id) {
        return GroupeDetailRest.depuisDomaine(gererGroupes.recuperer(id));
    }

    @GetMapping("/repositories")
    public List<RepositoryBitbucketRest> rechercherRepositories(@RequestParam String nom) {
        return rechercherRepositories.executer(nom).stream()
                .map(RepositoryBitbucketRest::depuisDomaine)
                .toList();
    }

    @GetMapping("/repositories/{project}/{slug}")
    public DetailRepositoryRest recupererDetailRepository(@PathVariable String project, @PathVariable String slug) {
        var detail = recupererDetailRepository.executer(project, slug);
        return new DetailRepositoryRest(
                detail.projet(),
                detail.slug(),
                detail.nom(),
                detail.versions().stream()
                        .map(version -> new VersionEnvironnementRest(version.environnement(), version.version()))
                        .toList()
        );
    }

    @PostMapping
    public GroupeDetailRest creer(@Valid @RequestBody DemandeGroupeRest demande) {
        return GroupeDetailRest.depuisDomaine(gererGroupes.creer(demande.versDomaine(null)));
    }

    @PutMapping("/{id}")
    public GroupeDetailRest modifier(@PathVariable UUID id, @Valid @RequestBody DemandeGroupeRest demande) {
        return GroupeDetailRest.depuisDomaine(gererGroupes.modifier(id, demande.versDomaine(id)));
    }

}
