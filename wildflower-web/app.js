var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
  var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
  return v.toString(16);
});

function getIndexFromPoint(point, count, gap) {
  return (point / gap) + (count / 2);
}

function getViewport(viewAngle, aspectRatio, cameraX, cameraY, cameraZ) {
  console.log(viewAngle, aspectRatio, cameraX, cameraY, cameraZ)
  var radianViewAngle = (viewAngle / 2) * (2*Math.PI / 360);
  var viewportHeight = 2 * cameraZ * Math.tan(radianViewAngle);
  var viewportWidth = aspectRatio * viewportHeight;

  var heightRadius = viewportHeight / 2;
  var widthRadius = viewportWidth / 2;

  return {
    upperLeft: { x: cameraX - widthRadius, y: cameraY - heightRadius },
    lowerRight: { x: cameraX + widthRadius, y: cameraY + heightRadius },
  };
}

function makePlane(terrainTile) {
  var gap = terrainTile.gap;
  var xCount = terrainTile.xCount - 1;
  var yCount = terrainTile.yCount - 1;
  var planeWidth = gap * xCount;
  var planeHeight = gap * yCount;

  var geometry = new THREE.PlaneGeometry(planeWidth, planeHeight, xCount, yCount);
  _.each(geometry.faces, function(face) {
    _.each(['a', 'b', 'c'], function(faceIndex, j) {
      vertexIndex = face[faceIndex];
      point = geometry.vertices[vertexIndex];

      color = new THREE.Color( 0xffffff );
      index = {
        x: getIndexFromPoint(point.x, xCount, terrainTile.gap),
        y: getIndexFromPoint(point.y, yCount, terrainTile.gap)
      };
      var value = terrainTile.terrain[index.x][index.y];


      switch (String.fromCharCode(value)) {
        case 'd':
          color.setRGB(0.75, 0.5, 0.1);
          break;
        case 'w':
          color.setRGB(0.2, 0.3, 0.6);
          break;
        case 'g':
          color.setRGB(0.2, 0.6, 0.3);
          break;
        case 's':
          color.setRGB(0.85, 0.6, 0.2);
          break;
      }
      face.vertexColors[j] = color;
    });
  });

  var material = new THREE.MeshBasicMaterial({ color: 0xffffff, vertexColors: THREE.VertexColors });
  var plane = new THREE.Mesh(geometry, material);

  var x = (terrainTile.index.x * planeWidth) + (planeWidth / 2);
  var y = (terrainTile.index.y * planeHeight) + (planeHeight / 2);
  plane.position.set(x, y, 0);
  return plane;
}

var scene = new THREE.Scene();

var SCREEN_WIDTH = window.innerWidth,
    SCREEN_HEIGHT = window.innerHeight;

var VIEW_ANGLE = 45,
    ASPECT = SCREEN_WIDTH / SCREEN_HEIGHT,
    NEAR = 0.1,
    FAR = 20000;

var camera = new THREE.PerspectiveCamera( VIEW_ANGLE, ASPECT, NEAR, FAR);
scene.add(camera);
camera.position.set(250,250,400);
scene.position.set(250, 250, 0);
camera.lookAt(scene.position);
var renderer = new THREE.WebGLRenderer({ antialias:true });

renderer.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
container = document.getElementById('viewport');
container.appendChild(renderer.domElement);

var light = new THREE.PointLight(0xffffff);
light.position.set(0,250,0);
scene.add(light);

renderer.render(scene, camera);

function connectWebSocket(path, client) {
  var socket = new WebSocket(`ws://${location.hostname}:${location.port}/${path}`);
  socket.onopen = function() { socket.send(JSON.stringify(client)); };
  return socket;
}

var client = {
    id: uuid,
    viewport: getViewport(VIEW_ANGLE, ASPECT, 250, 250, 400)
};

var entitySocket = connectWebSocket('entity', client);
var terrainSocket = connectWebSocket('terrain', client);
var viewportSocket = connectWebSocket('viewport', client);

terrainSocket.onmessage = function(message) {
  var data = {};
  try {
    data = JSON.parse(message.data);
  } catch(err) {
    console.log(`We could not parse out ${message.data}`);
  }
  console.log(data);
  var plane = makePlane(data.item);
  scene.add(plane);
  renderer.render(scene, camera);
}

var tempHeight = 400;
setInterval(function() {
  camera.position.set(250,250,tempHeight);
  renderer.render(scene, camera);
  tempHeight += 10;
  viewportSocket.send(JSON.stringify(getViewport(VIEW_ANGLE, ASPECT, 250, 250, tempHeight)));
}, 100)
