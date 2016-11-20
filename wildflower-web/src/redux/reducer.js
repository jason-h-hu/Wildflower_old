import * as _ from 'lodash';
import { combineReducers } from 'redux';
import { initialState } from './state';
import * as Action from './actions';

function indexToKey(index) {
  return `x:${index.x}y:${index.y}`;
}

export function terrain(state = initialState.terrain, action) {
  if (action.type !== Action.UPDATE_TERRAIN_TILE) {
    return state;
  }

  switch (action.data.change) {
    case 'ADD':
      return _.extend({}, state, {
        [indexToKey(action.data.item.index)]: action.data.item
      });
    case 'UDPATE':
    case 'REMOVE':
    default:
      return state;
  }
}

export const reducer = combineReducers({
	terrain,
});

