package com.devops.back.domain.port;

import com.devops.back.domain.commun.modele.TicketJira;

import java.util.List;

public interface PortJira {

    List<TicketJira> recupererTickets(List<String> cles);

    void ajouterEtiquetteLivraison(List<String> cles, String etiquette);
}
