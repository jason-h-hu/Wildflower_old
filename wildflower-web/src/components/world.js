import * as React from 'react';
import { connect } from 'react-redux';
import * as _ from 'lodash';
import * as THREE from 'three';
import { Renderer, Scene, Mesh } from 'react-three';

export class World extends React.Component {
  constructor() {
    super();
    this.scene = new THREE.Scene();
    var SCREEN_WIDTH = window.innerWidth, SCREEN_HEIGHT = window.innerHeight;
    var VIEW_ANGLE = 45;
    var ASPECT = SCREEN_WIDTH / SCREEN_HEIGHT;
    var NEAR = 0.1;
    var FAR = 20000;
    this.camera = new THREE.PerspectiveCamera( VIEW_ANGLE, ASPECT, NEAR, FAR);
    this.scene.add(this.camera);
    this.camera.position.set(0,150,400);
    this.camera.lookAt(this.scene.position);

    this.renderer = new THREE.WebGLRenderer({ antialias:true });
    this.renderer.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
  }

  render() {
    return <div />;
  }
}

function mapStateToProps(state) {
  return _.pick(state, ['terrain']);
}

export const ConnectedWorld = connect(mapStateToProps)(World);
