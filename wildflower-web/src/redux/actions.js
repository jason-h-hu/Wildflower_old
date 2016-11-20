export const UPDATE_TERRAIN_TILE = 0;

export function updateTerrainTile(data) {
  return {
    type: UPDATE_TERRAIN_TILE,
    data: data
  };
}
