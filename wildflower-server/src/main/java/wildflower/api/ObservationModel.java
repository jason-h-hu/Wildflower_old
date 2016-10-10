package wildflower.api;

import wildflower.creature.Creature;

import java.util.stream.Collectors;
import java.util.Set;

public class ObservationModel {
    public Set<CreatureObservationModel> creatures;

    public ObservationModel() {
        // No arg constructor for Gson
    }

    public ObservationModel(Set<Creature> creatures) {
        this.creatures = creatures.stream().map(CreatureObservationModel::new).collect(Collectors.toSet());
    }
}
