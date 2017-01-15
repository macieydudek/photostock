package pl.com.bottega.photostock.sales.infrastructure.memory;

import pl.com.bottega.photostock.sales.model.client.Client;
import pl.com.bottega.photostock.sales.model.lightbox.LightBox;
import pl.com.bottega.photostock.sales.model.lightbox.LightBoxRepository;

import java.util.*;

public class InMemoryLightBoxRepository implements LightBoxRepository {
    private static final Map<Client, Collection<LightBox>> REPOSITORY = new HashMap<>();

    @Override
    public void put(LightBox lightBox) {
        Client owner = lightBox.getOwner();
        REPOSITORY.putIfAbsent(owner, new HashSet<>());
        REPOSITORY.get(owner).add(lightBox);
    }

    @Override
    public Collection<LightBox> getFor(Client client) {
        Collection<LightBox> temporaryStorage = new HashSet<>();

        if (REPOSITORY.containsKey(client))
            temporaryStorage.addAll(REPOSITORY.get(client));
        else
            throw new IllegalArgumentException("There are no LightBoxes stored for this client.");

        return temporaryStorage;
    }

    @Override
    public Collection<String> getLightBoxNames(Client client) {
        Collection<String> lightBoxNames = new LinkedList<>();
        Collection<LightBox> lightBoxes = REPOSITORY.get(client);
        if(lightBoxes != null)
            for(LightBox lb : lightBoxes)
                lightBoxNames.add(lb.getName());
        return lightBoxNames;
    }

    @Override
    public LightBox findLightBox(Client client, String lightBoxName) {
        Collection<LightBox> lightBoxes = REPOSITORY.get(client);
        if(lightBoxes != null)
            for(LightBox lb : lightBoxes)
                if(lb.getName().equals(lightBoxName))
                    return lb;
        return null;
    }
}
