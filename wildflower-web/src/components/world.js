import * as React from 'react';
import { connect } from 'react-redux';
import * as _ from 'lodash';
import * as THREE from 'three';

export class World extends React.Component {
    render() {
      return <p>{JSON.stringify(this.props.terrain)}</p>
    }
}

function mapStateToProps(state) {
  console.log(state)
  return _.pick(state, ['terrain']);
}

export const ConnectedWorld = connect(mapStateToProps)(World);
