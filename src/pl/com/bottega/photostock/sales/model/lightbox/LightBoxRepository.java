package pl.com.bottega.photostock.sales.model.lightbox;

import pl.com.bottega.photostock.sales.model.client.Client;

import java.util.Collection;

public interface LightBoxRepository {

    void put(LightBox l);

    Collection<LightBox> getFor(Client client);

    LightBox findLightBox(Client client, String lightBoxName);

    Collection<String> getLightBoxNames(Client client);
}

