import { combineReducers } from 'redux';
import { initialState } from './state';
import * as Action from './actions';

export function messages(state = initialState.messages, action) {
    switch (action.type) {
        case Action.ADD_MESSAGE:
            return state.concat(action.message);
        default:
            return state;
    }
}

export const reducer = combineReducers({
    messages
});
