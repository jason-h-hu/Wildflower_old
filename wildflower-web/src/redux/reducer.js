import { combineReducers } from 'redux';
import { initialState } from './state';
import * as Action from './actions';

export function messages(state = initialState.messages, action) {
	switch (action.type) {
		case Action.ADD_MESSAGE:
			return [action.message];
		default:
			return state;
	}
}

export function entities(state = initialState.entities, action) {
	return action.type === Action.SET_ENTITIES ? action.entities : state;
}

export const reducer = combineReducers({
	messages,
	entities
});
