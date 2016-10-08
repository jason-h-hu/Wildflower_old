import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { createStore } from 'redux';
import { ConnectedApp } from './components/app';
import { reducer } from './redux/reducer';
import * as Action from './redux/actions';

const appElement = document.getElementById('app');
const store = createStore(reducer);

var socket = new WebSocket(`ws://${location.hostname}:${location.port}/wildflower`);
socket.onmessage = function(message) {
  store.dispatch(Action.addMessage(message));
  console.log(message.data)
  store.dispatch(Action.setEntities(JSON.parse(message.data)));
}

if (appElement !== null) {
  ReactDOM.render((
    <Provider store={store}>
      <ConnectedApp />
    </Provider>
  ), appElement);
}
