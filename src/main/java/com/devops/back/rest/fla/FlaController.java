package com.devops.back.rest.fla;

import com.devops.back.domain.fla.casusage.RechercherFichesLivraison;
import com.devops.back.rest.fla.model.FicheLivraisonRest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fla")
public class FlaController {

    private final RechercherFichesLivraison rechercherFichesLivraison;

    public FlaController(RechercherFichesLivraison rechercherFichesLivraison) {
        this.rechercherFichesLivraison = rechercherFichesLivraison;
    }

    @GetMapping
    public List<FicheLivraisonRest> rechercher(
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return rechercherFichesLivraison.executer(start, limit).stream()
                .map(FicheLivraisonRest::depuisDomaine)
                .toList();
    }

}
