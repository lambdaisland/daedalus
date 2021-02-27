/**
 * @fileoverview Public API of hxDaedalus.js.
 * @externs
 */

const hxDaedalus = {};
goog.global.hxDaedalus = hxDaedalus;

hxDaedalus.factories.BitmapMesh = class {
  constructor() {
  }
};
hxDaedalus.factories.BitmapMesh.buildFromBmpData = function() {};

hxDaedalus.factories.BitmapObject = class {
  constructor() {
  }
};
hxDaedalus.factories.BitmapObject.buildFromBmpData = function() {};

hxDaedalus.factories.RectMesh = class {
  constructor() {
  }
};
hxDaedalus.factories.RectMesh.buildRectangle = function() {};

hxDaedalus.data.Mesh = class {
  constructor() {
    this._vertices;
    this._edges;
    this._faces;
  }
  set_clipping() {};
  get_height() {};
  get_width() {};
  get_clipping() {};
  get_id() {};
  get___constraintShapes() {};
  restoreAsDelaunay() {};
  flipEdge() {};
  insertObject() {};
  updateObjects() {};
  dispose() {};
  insertConstraintShape() {};
  splitFace() {};
  deleteConstraintShape() {};
  getVerticesAndEdges() {};
  vertexIsInsideAABB() {};
  debug() {};
  insertVertex() {};
  splitEdge() {};
  buildFromRecord() {};
  insertConstraintSegment() {};
  deleteConstraintSegment() {};
  deleteObject() {};
  deleteVertex() {};
};

hxDaedalus.data.ConstraintShape = class {
  constructor() {
    this.segments;
  }
  get_id() {};
  dispose() {};
};

hxDaedalus.data.Constants = class {
  constructor() {
  }
};

hxDaedalus.data.Edge = class {
  constructor() {
    this.fromConstraintSegments;
    this.colorDebug;
  }
  set_isConstrained() {};
  set_originVertex() {};
  set_nextLeftEdge() {};
  set_leftFace() {};
  get_id() {};
  get_isReal() {};
  get_isConstrained() {};
  get_originVertex() {};
  get_nextLeftEdge() {};
  get_leftFace() {};
  get_destinationVertex() {};
  get_oppositeEdge() {};
  get_prevLeftEdge() {};
  get_nextRightEdge() {};
  get_prevRightEdge() {};
  get_rotLeftEdge() {};
  get_rotRightEdge() {};
  get_rightFace() {};
  dispose() {};
  toString() {};
  addFromConstraintSegment() {};
  removeFromConstraintSegment() {};
  setDatas() {};
};

hxDaedalus.data.Face = class {
  constructor() {
    this.colorDebug;
  }
  get_id() {};
  get_isReal() {};
  get_edge() {};
  dispose() {};
  setDatas() {};
};

hxDaedalus.data.graph.Graph = class {
  constructor() {
  }
  get_id() {};
  get_edge() {};
  get_node() {};
  deleteEdge() {};
  dispose() {};
  insertEdge() {};
  deleteNode() {};
  insertNode() {};
};

hxDaedalus.data.graph.GraphNode = class {
  constructor() {
  }
  set_prev() {};
  set_next() {};
  set_outgoingEdge() {};
  set_successorNodes() {};
  set_data() {};
  get_id() {};
  get_prev() {};
  get_next() {};
  get_outgoingEdge() {};
  get_successorNodes() {};
  get_data() {};
  dispose() {};
};

hxDaedalus.data.graph.GraphEdge = class {
  constructor() {
  }
  set_prev() {};
  set_next() {};
  set_rotPrevEdge() {};
  set_rotNextEdge() {};
  set_oppositeEdge() {};
  set_sourceNode() {};
  set_destinationNode() {};
  set_data() {};
  get_id() {};
  get_prev() {};
  get_next() {};
  get_rotPrevEdge() {};
  get_rotNextEdge() {};
  get_oppositeEdge() {};
  get_sourceNode() {};
  get_destinationNode() {};
  get_data() {};
  dispose() {};
};

hxDaedalus.data.Vertex = class {
  constructor() {
    this.colorDebug;
  }
  set_fromConstraintSegments() {};
  set_edge() {};
  get_id() {};
  get_isReal() {};
  get_pos() {};
  get_fromConstraintSegments() {};
  get_edge() {};
  dispose() {};
  toString() {};
  addFromConstraintSegment() {};
  removeFromConstraintSegment() {};
  setDatas() {};
};

hxDaedalus.data.ConstraintSegment = class {
  constructor() {
    this.fromShape;
  }
  get_id() {};
  get_edges() {};
  dispose() {};
  toString() {};
  removeEdge() {};
  addEdge() {};
};

hxDaedalus.data.Object = class {
  constructor() {
  }
  set_pivotX() {};
  set_pivotY() {};
  set_scaleX() {};
  set_scaleY() {};
  set_rotation() {};
  set_x() {};
  set_y() {};
  set_matrix() {};
  set_coordinates() {};
  set_constraintShape() {};
  set_hasChanged() {};
  get_id() {};
  get_pivotX() {};
  get_pivotY() {};
  get_scaleX() {};
  get_scaleY() {};
  get_rotation() {};
  get_x() {};
  get_y() {};
  get_matrix() {};
  get_coordinates() {};
  get_constraintShape() {};
  get_hasChanged() {};
  get_edges() {};
  updateValuesFromMatrix() {};
  dispose() {};
  updateMatrixFromValues() {};
};

hxDaedalus.data.math.EdgeData = class {
  constructor() {
    this.sumDistancesSquared;
    this.length;
    this.nodesCount;
  }
};

hxDaedalus.data.math.NodeData = class {
  constructor() {
    this.index;
    this.point;
  }
};

hxDaedalus.data.math.RandGenerator = class {
  constructor() {
    this.rangeMin;
    this.rangeMax;
  }
  set_seed() {};
  get_seed() {};
  next() {};
  shuffle() {};
  reset() {};
  nextInRange() {};
};

hxDaedalus.data.math.Matrix2D = class {
  constructor() {
    this.a;
    this.b;
    this.c;
    this.d;
    this.e;
    this.f;
  }
  translate() {};
  scale() {};
  transformY() {};
  transformX() {};
  identity() {};
  concat() {};
  clone() {};
  tranform() {};
  rotate() {};
};

hxDaedalus.data.math.Point2D = class {
  constructor() {
    this.x;
    this.y;
  }
  get_length() {};
  normalize() {};
  scale() {};
  transform() {};
  distanceTo() {};
  clone() {};
  substract() {};
  setXY() {};
};

hxDaedalus.data.math.Potrace = class {
  constructor() {
  }
};
hxDaedalus.data.math.Potrace.buildShape = function() {};
hxDaedalus.data.math.Potrace.buildGraph = function() {};
hxDaedalus.data.math.Potrace.buildShapes = function() {};
hxDaedalus.data.math.Potrace.buildPolygon = function() {};

hxDaedalus.data.math.Tools = class {
  constructor() {
  }
  extractMeshFromBitmap() {};
  extractMeshFromShapes() {};
};

hxDaedalus.data.math.ShapeSimplifier = class {
  constructor() {
  }
  simplify() {};
};

hxDaedalus.data.math.Geom2D = class {
  constructor() {
  }
};
hxDaedalus.data.math.Geom2D.distanceSquaredVertexToEdge = function() {};
hxDaedalus.data.math.Geom2D.getDirection2 = function() {};
hxDaedalus.data.math.Geom2D.intersectionsSegmentCircle = function() {};
hxDaedalus.data.math.Geom2D.isConvex = function() {};
hxDaedalus.data.math.Geom2D.pathLength = function() {};
hxDaedalus.data.math.Geom2D.isInFace = function() {};
hxDaedalus.data.math.Geom2D.intersectionsLineCircle = function() {};
hxDaedalus.data.math.Geom2D.getDirection = function() {};
hxDaedalus.data.math.Geom2D.locatePosition = function() {};
hxDaedalus.data.math.Geom2D.tangentsCrossCircleToCircle = function() {};
hxDaedalus.data.math.Geom2D.intersections2edges = function() {};
hxDaedalus.data.math.Geom2D.getCircumcenter = function() {};
hxDaedalus.data.math.Geom2D.projectOrthogonaly = function() {};
hxDaedalus.data.math.Geom2D.distanceSquaredPointToSegment = function() {};
hxDaedalus.data.math.Geom2D.isCircleIntersectingAnyConstraint = function() {};
hxDaedalus.data.math.Geom2D.intersections2Circles = function() {};
hxDaedalus.data.math.Geom2D.clipSegmentByTriangle = function() {};
hxDaedalus.data.math.Geom2D.getRelativePosition2 = function() {};
hxDaedalus.data.math.Geom2D.tangentsPointToCircle = function() {};
hxDaedalus.data.math.Geom2D.isSegmentIntersectingTriangle = function() {};
hxDaedalus.data.math.Geom2D.isDelaunay = function() {};
hxDaedalus.data.math.Geom2D.distanceSquaredPointToLine = function() {};
hxDaedalus.data.math.Geom2D.intersections2segments = function() {};
hxDaedalus.data.math.Geom2D.getRelativePosition = function() {};
hxDaedalus.data.math.Geom2D.tangentsParalCircleToCircle = function() {};

hxDaedalus.iterators.FromEdgeToRotatedEdges = class {
  constructor() {
  }
};

hxDaedalus.iterators.FromMeshToVertices = class {
  constructor() {
  }
  set_fromMesh() {};
  next() {};
};

hxDaedalus.iterators.FromVertexToOutgoingEdges = class {
  constructor() {
    this.realEdgesOnly;
  }
  set_fromVertex() {};
  next() {};
};

hxDaedalus.iterators.FromFaceToInnerVertices = class {
  constructor() {
  }
  set_fromFace() {};
  next() {};
};

hxDaedalus.iterators.FromVertexToHoldingFaces = class {
  constructor() {
  }
  set_fromVertex() {};
  next() {};
};

hxDaedalus.iterators.FromVertexToNeighbourVertices = class {
  constructor() {
  }
  set_fromVertex() {};
  next() {};
};

hxDaedalus.iterators.FromFaceToInnerEdges = class {
  constructor() {
  }
  set_fromFace() {};
  next() {};
};

hxDaedalus.iterators.FromVertexToIncomingEdges = class {
  constructor() {
  }
  set_fromVertex() {};
  next() {};
};

hxDaedalus.iterators.FromFaceToNeighbourFaces = class {
  constructor() {
  }
  set_fromFace() {};
  next() {};
};

hxDaedalus.iterators.FromMeshToFaces = class {
  constructor() {
  }
  set_fromMesh() {};
  next() {};
};

hxDaedalus.ai.PathFinder = class {
  constructor() {
    this.entity;
  }
  set_mesh() {};
  get_mesh() {};
  dispose() {};
  findPath() {};
};

hxDaedalus.ai.EntityAI = class {
  constructor() {
    this.dirNormY;
    this.dirNormX;
    this.y;
    this.x;
  }
  set_radius() {};
  get_approximateObject() {};
  get_radius() {};
  get_radiusSquared() {};
  buildApproximation() {};
};

hxDaedalus.ai.AStar = class {
  constructor() {
  }
  set_radius() {};
  set_mesh() {};
  get_radius() {};
  dispose() {};
  findPath() {};
};

hxDaedalus.ai.trajectory.PathIterator = class {
  constructor() {
  }
  set_entity() {};
  set_path() {};
  get_entity() {};
  get_x() {};
  get_y() {};
  get_hasPrev() {};
  get_hasNext() {};
  get_count() {};
  get_countMax() {};
  next() {};
  reset() {};
  prev() {};
};

hxDaedalus.ai.trajectory.LinearPathSampler = class {
  constructor() {
    this.entity;
  }
  set_count() {};
  set_samplingDistance() {};
  set_path() {};
  get_x() {};
  get_y() {};
  get_hasPrev() {};
  get_hasNext() {};
  get_count() {};
  get_countMax() {};
  get_samplingDistance() {};
  next() {};
  dispose() {};
  reset() {};
  prev() {};
  preCompute() {};
};

hxDaedalus.ai.Funnel = class {
  constructor() {
    this.debugSurface;
  }
  set_radius() {};
  get_radius() {};
  dispose() {};
  getCopyPoint() {};
  getPoint() {};
  findPath() {};
};

hxDaedalus.view.SimpleView = class {
  constructor() {
    this.edgesColor;
    this.edgesWidth;
    this.edgesAlpha;
    this.constraintsColor;
    this.constraintsWidth;
    this.constraintsAlpha;
    this.verticesColor;
    this.verticesRadius;
    this.verticesAlpha;
    this.pathsColor;
    this.pathsWidth;
    this.pathsAlpha;
    this.entitiesColor;
    this.entitiesWidth;
    this.entitiesAlpha;
    this.faceColor;
    this.faceWidth;
    this.faceAlpha;
  }
  drawEdge() {};
  drawEntities() {};
  drawVertex() {};
  drawMesh() {};
  drawEntity() {};
  drawPath() {};
  drawFace() {};
  refreshGraphics2D() {};
};

hxDaedalus.debug.Debug = class {
  constructor() {
  }
  trace() {};
  assertEquals() {};
  assertFalse() {};
  assertTrue() {};
};

