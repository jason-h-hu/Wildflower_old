import * as React from 'react';
import { connect } from 'react-redux';
import { ConnectedWorld } from './world';

export class App extends React.Component {
    render() {
        return (
            <div>
                <ConnectedWorld/>
            </div>
        );
    }
}

function mapStateToProps(state) {
    return {};
}

export const ConnectedApp = connect(mapStateToProps)(App);
