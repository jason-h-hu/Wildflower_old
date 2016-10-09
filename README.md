# Wildflower [![CircleCI](https://circleci.com/gh/jason-h-hu/Wildflower/tree/master.svg?style=shield)](https://circleci.com/gh/jason-h-hu/Wildflower/tree/master)

Wildflower is a sandbox game about creatures. Creatures in wildflower are controlled by Agents, which are programs written by Wildflower players. Agents must interact with the Wildflower HTTP API in order to manipulate their Creature, and observe the Wildflower World through its eyes. It is composed of:

## Wildflower Core
The Wildflower Core is a physical simulation of the mechanics of the Wildflower world. It manages Creatures and their physical interactions with their environment and each other.

## Wildflower Web
Wildflower Web is a web application which visualizes the world of Wildflower

## Wildflower Server
Wildflower Server runs an instance of the Wildflower World and exposes an HTTP API with which Agents can acquire and control Creatures. It also hosts and communicates with the Wildflower Web application via WebSockets for real time rendering.

## Wildflower Agents
Wildflower Agents are the brains of Creatures in Wildflower. The `wildflower-agents` directory contains some example implementations in various languages.

