import * as React from 'react';
import { connect } from 'react-redux';
import { ConnectedWorld } from './world';

export class App extends React.Component {
    render() {
        const messageElements = this.props.messages.map(function(message) {
            return <div>{message.data}</div>;
        });

        return (
            <div>
                <ConnectedWorld/>
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
