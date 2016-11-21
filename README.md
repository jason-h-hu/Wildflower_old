# Wildflower [![CircleCI](https://circleci.com/gh/jason-h-hu/Wildflower/tree/master.svg?style=shield)](https://circleci.com/gh/jason-h-hu/Wildflower/tree/master)

\# [Spark](http://sparkjava.com/) / [Gson](https://github.com/google/gson) / [JOML](http://joml-ci.github.io/JOML/) / [WebSockets](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API) / [Three.js](https://threejs.org/)

Wildflower is a sandbox game about intelligent, evolving creatures. Creatures in wildflower are controlled by Agents, which are programs written by Wildflower players. Agents interact with the Wildflower Server via HTTP in order to manipulate their Creature, and observe the Wildflower World through its eyes.

For more information, check out [The Wiki](https://github.com/jason-h-hu/Wildflower/wiki)!

### How To Run
Run `./gradlew :wildflower-server:run` to start the server, then navigate in browser to `http://localhost:9090`. Requires a modern browser which supports WebSockets.

### Inspiration
[Robocode](http://robocode.sourceforge.net/), [BoxCar2D](http://boxcar2d.com/), [Spore](http://www.spore.com/), [Soda Constructor](https://en.wikipedia.org/wiki/Soda_Constructor), [Karl Sims](http://www.karlsims.com/evolved-virtual-creatures.html), [Braitenberg Vehicles](https://en.wikipedia.org/wiki/Braitenberg_vehicle)
