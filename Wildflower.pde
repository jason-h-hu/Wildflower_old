import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

int SIZE = 2;
int W = 500;
int H = 400;
World world; 

Set<Agent> agents = new HashSet<Agent>();

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
  UUID id = world.addCreature(new PVector(mouseX, mouseY));
  Agent agent = new Agent(world, id);
  updateOrActAgents(agent);
}

void agentThread() {
  while(true) {
    updateOrActAgents(null);
    try {
      Thread.sleep(200);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}

public synchronized void updateOrActAgents(Agent agent) {
  if (agent != null) {
    agents.add(agent);
  } else {
    for (Agent a : agents) {
      a.act();
    }
  }
}