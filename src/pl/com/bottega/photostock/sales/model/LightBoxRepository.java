package pl.com.bottega.photostock.sales.model;

import java.util.Collection;

public interface LightBoxRepository {

    void put(LightBox l);

    Collection<LightBox> getFor(Client client);

}

