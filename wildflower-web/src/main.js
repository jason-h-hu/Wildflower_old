import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import { ConnectedApp } from './components/app';
import { reducer } from './redux/reducer';
import * as Action from './redux/actions';

const appElement = document.getElementById('app');
const store = createStore(reducer);

// courtesy of http://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript
const uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
  var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
  return v.toString(16);
});

const client = {
    id: uuid,
    viewport: {
        upperLeft: { x: 0, y: 0 },
        lowerRight: { x: 100, y: 100 }
    }
};

function connectWebSocket(path) {
  var socket = new WebSocket(`ws://${location.hostname}:${location.port}/${path}`);
  socket.onopen = function() { socket.send(JSON.stringify(client)); };
  return socket;
}

var entitySocket = connectWebSocket('entity');
var terrainSocket = connectWebSocket('terrain');
var viewportSocket = connectWebSocket('viewport');

terrainSocket.onmessage = function(message) {
  var data = {};
  try {
    data = JSON.parse(message.data);
  } catch(err) {
    console.log(`We could not parse out ${message.data}`);
  }
  return store.dispatch(Action.updateTerrainTile(data));
}

if (appElement !== null) {
  ReactDOM.render((
    <Provider store={store}>
      <ConnectedApp />
    </Provider>
  ), appElement);
}
