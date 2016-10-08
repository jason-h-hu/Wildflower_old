export const ADD_MESSAGE = 0;

export function addMessage(message) {
    return {
        type: ADD_MESSAGE,
        message: message
    };
}
