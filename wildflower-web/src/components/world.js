import * as React from 'react';
import { connect } from 'react-redux';

export class World extends React.Component {
    render() {
        const entities = this.props.entities.map(entity => <circle cx={entity.location.x} cy={entity.location.y} r="10" />);
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
