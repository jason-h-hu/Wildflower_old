import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

int SIZE = 2;
int W = 500;
int H = 400;
World world; 

private Set<Agent> agents = new HashSet<Agent>();

void settings() {
  size(W*SIZE, H*SIZE);
  world = new World(W*SIZE, H*SIZE);
  thread("agentThread");
}

void draw() {
  world.update();
  world.render();
}

void mouseClicked() {
  Creature creature = new Creature(new PVector(mouseX, mouseY));
  UUID id = creature.getID();
  CreatureAPI creatureAPI = new CreatureAPI(world, creature, id);
  Agent agent = new Agent(creatureAPI, id);  
  world.addCreature(creature);
  agents.add(agent);
}

void agentThread() {
  while(true) {
    for (Agent agent : this.agents) {
      System.out.println("boop");
      agent.act();
    }
  }
}