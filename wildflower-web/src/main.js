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

var entitySocket = new WebSocket(`ws://${location.hostname}:${location.port}/entity`);
entitySocket.onopen = function() { entitySocket.send(uuid); };

var viewportSocket = new WebSocket(`ws://${location.hostname}:${location.port}/viewport`);
viewportSocket.onopen = function() { viewportSocket.send(uuid); };

entitySocket.onmessage = function(message) {
  store.dispatch(Action.setEntities(JSON.parse(message.data)));
}

if (appElement !== null) {
  ReactDOM.render((
    <Provider store={store}>
      <ConnectedApp />
    </Provider>
  ), appElement);
}
