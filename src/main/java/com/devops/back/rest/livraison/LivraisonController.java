package com.devops.back.rest.livraison;

import com.devops.back.domain.livraison.casusage.AnalyserLivraison;
import com.devops.back.domain.livraison.casusage.GenererLivraison;
import com.devops.back.rest.livraison.model.AnalyseLivraisonRest;
import com.devops.back.rest.livraison.model.DemandeAnalyseLivraisonRest;
import com.devops.back.rest.livraison.model.DemandeGenerationLivraisonRest;
import com.devops.back.rest.livraison.model.LivraisonGenereeRest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/livraisons")
public class LivraisonController {

    private final AnalyserLivraison analyserLivraison;
    private final GenererLivraison genererLivraison;

    public LivraisonController(AnalyserLivraison analyserLivraison, GenererLivraison genererLivraison) {
        this.analyserLivraison = analyserLivraison;
        this.genererLivraison = genererLivraison;
    }

    @PostMapping("/analyse")
    public List<AnalyseLivraisonRest> analyser(@Valid @RequestBody DemandeAnalyseLivraisonRest demande) {
        return analyserLivraison.executer(demande.versDomaine()).stream()
                .map(AnalyseLivraisonRest::depuisDomaine)
                .toList();
    }

    @PostMapping
    public LivraisonGenereeRest generer(
            @Valid @RequestBody DemandeGenerationLivraisonRest demande,
            @RequestHeader(name = "X-Auteur", defaultValue = "Utilisateur inconnu") String auteur,
            @RequestParam(required = false) String etiquetteLivraison
    ) {
        return LivraisonGenereeRest.depuisDomaine(
                genererLivraison.executer(demande.versDomaine(), auteur, etiquetteLivraison)
        );
    }
}
