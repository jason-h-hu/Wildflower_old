import * as React from 'react';
import { connect } from 'react-redux';

export class App extends React.Component {
    render() {
        const messageElements = this.props.messages.map(function(message) {
            return <div>{message.data}</div>;
        });

        return (
            <div>
                {messageElements}
            </div>
        );
    }
}

function mapStateToProps(state) {
    return {
        messages: state.messages
    };
}

export const ConnectedApp = connect(mapStateToProps)(App);
