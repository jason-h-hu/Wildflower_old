export const ADD_MESSAGE = 0;
export const SET_ENTITIES = 1;

export function addMessage(message) {
  return {
    type: ADD_MESSAGE,
    message: message
  };
}

export function setEntities(entities) {
  return {
    type: SET_ENTITIES,
    entities: entities
  };
}
