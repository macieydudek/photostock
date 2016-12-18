package pl.com.bottega.photostock.sales.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class InMemoryLightBoxRepository implements LightBoxRepository {
    private Map<Client, Collection<LightBox>> REPOSITORY = new HashMap<>();

    @Override
    public void put(LightBox l) {
        if(!REPOSITORY.containsKey(l.getOwner())) { //można metodę mapy "putIfAbsent"
            REPOSITORY.put(l.getOwner(), new HashSet<LightBox>()); //można wyciągnąć l.getOwner() do zmiennej owner
        }
        REPOSITORY.get(l.getOwner()).add(l);
    }

    @Override
    public Collection<LightBox> getFor(Client client) {
        return REPOSITORY.get(client);
    } //tutaj można defensywną kopię

}
