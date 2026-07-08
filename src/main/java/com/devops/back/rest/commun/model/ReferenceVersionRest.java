package com.devops.back.rest.commun.model;

import com.devops.back.domain.commun.modele.ReferenceVersion;
import com.devops.back.domain.commun.modele.TypeReference;
import jakarta.validation.constraints.NotBlank;

import java.util.Locale;

public record ReferenceVersionRest(
        @NotBlank String type,
        @NotBlank String value
) {

    public ReferenceVersion versDomaine() {
        return new ReferenceVersion(versTypeReference(type), value);
    }

    public static ReferenceVersionRest depuisDomaine(ReferenceVersion referenceVersion) {
        if (referenceVersion == null) {
            return null;
        }
        return new ReferenceVersionRest(depuisTypeReference(referenceVersion.type()), referenceVersion.valeur());
    }

    private static TypeReference versTypeReference(String type) {
        return switch (type.toUpperCase(Locale.ROOT)) {
            case "BRANCHE", "BRANCH" -> TypeReference.BRANCHE;
            case "ENV", "ENVIRONNEMENT" -> TypeReference.ENVIRONNEMENT;
            case "TAG" -> TypeReference.TAG;
            default -> throw new IllegalArgumentException("Type de reference inconnu : " + type);
        };
    }

    private static String depuisTypeReference(TypeReference typeReference) {
        return switch (typeReference) {
            case BRANCHE -> "branche";
            case ENVIRONNEMENT -> "ENV";
            case TAG -> "tag";
        };
    }
}
