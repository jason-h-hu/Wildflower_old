import * as React from 'react';
import { connect } from 'react-redux';

export class World extends React.Component {
    render() {
        const entities = this.props.entities.map(entity => <circle cx={entity.x} cy={entity.y} r="10"></circle>);
        const style = {width: "100%", height: "100%"};
        return <svg style={style}>{entities}</svg>;
    }
}

function mapStateToProps(state) {
    return {
        entities: state.entities
    };
}

export const ConnectedWorld = connect(mapStateToProps)(World);
