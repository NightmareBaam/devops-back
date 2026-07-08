package com.devops.back.infra.bitbucket;

import com.devops.back.domain.livraison.modele.ModificationConfiguration;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ComparateurYaml {

    public List<ModificationConfiguration> comparer(String ancienYaml, String nouveauYaml) {
        var ancien = aplatir(charger(ancienYaml));
        var nouveau = aplatir(charger(nouveauYaml));
        var cles = new ArrayList<String>();
        cles.addAll(ancien.keySet());
        nouveau.keySet().stream()
                .filter(cle -> !cles.contains(cle))
                .forEach(cles::add);

        return cles.stream()
                .filter(cle -> !Objects.equals(ancien.get(cle), nouveau.get(cle)))
                .map(cle -> new ModificationConfiguration(cle, ancien.get(cle), nouveau.get(cle)))
                .toList();
    }

    private Map<String, Object> charger(String yaml) {
        if (yaml == null || yaml.isBlank()) {
            return Map.of();
        }
        var charge = new Yaml().load(yaml);
        if (charge instanceof Map<?, ?> map) {
            return convertirMap(map);
        }
        return Map.of();
    }

    private Map<String, Object> convertirMap(Map<?, ?> source) {
        var resultat = new LinkedHashMap<String, Object>();
        source.forEach((cle, valeur) -> resultat.put(String.valueOf(cle), valeur));
        return resultat;
    }

    private Map<String, Object> aplatir(Map<String, Object> source) {
        var resultat = new LinkedHashMap<String, Object>();
        source.forEach((cle, valeur) -> aplatir(cle, valeur, resultat));
        return resultat;
    }

    private void aplatir(String prefixe, Object valeur, Map<String, Object> resultat) {
        if (valeur instanceof Map<?, ?> map) {
            map.forEach((sousCle, sousValeur) -> aplatir(prefixe + "." + sousCle, sousValeur, resultat));
        } else {
            resultat.put(prefixe, valeur);
        }
    }
}
