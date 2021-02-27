(ns lambdaisland.daedalus
  (:require ["hxdaedalus-js" :as daedalus]
            [applied-science.js-interop :as j]
            [lambdaisland.data-printers :as data-printers]))

;; Difference between vanilla-cljs and shadow, shadow loads the package, which
;; contains a `hxDaedalus` property. Vanilla-js includes the hxDaedalus.js,
;; which defines hxDaedalus globally, which we then alias to "hxdaedalus-js"
;; required above, via `:global-exports` in `deps.cljs`.
(when-not daedalus/hxDaedalus
  (set! (.-hxDaedalus daedalus) daedalus))

(defn to-edn [plain-vars getters]
  (let [vars (remove #(= "_" (first (name %)))
                     (concat plain-vars getters))]
    (fn [obj]
      (into {} (map (juxt identity #(try
                                      (get obj %)
                                      (catch :default e
                                        ::error)))) vars))))

(defn setup-type [type tag plain-vars getters]
  ;; The Haxe compiled code isn't idiomatic JavaScript, this is needed so
  ;; instances are recognized as being of the right type
  (j/assoc-in! type [:prototype :constructor] type)

  ;; Nice printers, intended for dev/repl convenience
  (when ^boolean js/goog.DEBUG
    (data-printers/register-print type tag (to-edn plain-vars getters))
    (data-printers/register-pprint type tag (to-edn plain-vars getters))
    ;; More JS weirdness, the (.-name type) on these constructors is "", make
    ;; sure that if we ask ClojureScript the `(type ...)` of something we see
    ;; something sensible
    (specify! type
      IPrintWithWriter
      (-pr-writer [_ writer _]
        (-write writer (name tag)))))

  ;; Keyword access of all Daedalus public vars and getters
  (let [plain-vars (set plain-vars)
        getters (set getters)]
    (extend-type type
      ILookup
      (-lookup
        ([^js this k]
         (cond
           (contains? plain-vars k)
           (j/get this k)
           (contains? getters k)
           (.call ^js (j/get this (str "get_" (name k))) this)))
        ([^js this k not-found]
         (cond
           (contains? plain-vars k)
           (j/get this k)
           (contains? getters k)
           (.call ^js (j/get this (str "get_" (name k))) this)
           :else
           not-found)))
      ;; Also support `keys` and `vals`
      ISeqable
      (-seq [this]
        (map #(MapEntry. % (get this %) nil) (concat plain-vars getters))))))

(defn- extend-keys!
  "Set properties in bulk, used in constructors"
  [obj opts keys]
  (doseq [k keys
          :let [v (get opts k ::not-found)]]
    (when (not= ::not-found v)
      (j/assoc! obj k v))))

;; Object types

(def AStar daedalus/hxDaedalus.ai.AStar)
(def EntityAI daedalus/hxDaedalus.ai.EntityAI)
(def Funnel daedalus/hxDaedalus.ai.Funnel)
(def PathFinder daedalus/hxDaedalus.ai.PathFinder)
(def LinearPathSampler daedalus/hxDaedalus.ai.trajectory.LinearPathSampler)
(def PathIterator daedalus/hxDaedalus.ai.trajectory.PathIterator)

(def Constants daedalus/hxDaedalus.data.Constants)
(def ConstraintSegment daedalus/hxDaedalus.data.ConstraintSegment)
(def ConstraintShape daedalus/hxDaedalus.data.ConstraintShape)
(def Edge daedalus/hxDaedalus.data.Edge)
(def Face daedalus/hxDaedalus.data.Face)
(def Mesh daedalus/hxDaedalus.data.Mesh)
(def Object daedalus/hxDaedalus.data.Object)
(def Vertex daedalus/hxDaedalus.data.Vertex)
(def Graph daedalus/hxDaedalus.data.graph.Graph)
(def GraphEdge daedalus/hxDaedalus.data.graph.GraphEdge)
(def GraphNode daedalus/hxDaedalus.data.graph.GraphNode)
(def EdgeData daedalus/hxDaedalus.data.math.EdgeData)
(def Geom2D daedalus/hxDaedalus.data.math.Geom2D)
(def Matrix2D daedalus/hxDaedalus.data.math.Matrix2D)
(def NodeData daedalus/hxDaedalus.data.math.NodeData)
(def Point2D daedalus/hxDaedalus.data.math.Point2D)
(def Potrace daedalus/hxDaedalus.data.math.Potrace)
(def RandGenerator daedalus/hxDaedalus.data.math.RandGenerator)
(def ShapeSimplifier daedalus/hxDaedalus.data.math.ShapeSimplifier)
(def Tools daedalus/hxDaedalus.data.math.Tools)

(def BitmapMesh daedalus/hxDaedalus.factories.BitmapMesh)
(def BitmapObject daedalus/hxDaedalus.factories.BitmapObject)
(def RectMesh daedalus/hxDaedalus.factories.RectMesh)

(def FromEdgeToRotatedEdges daedalus/hxDaedalus.iterators.FromEdgeToRotatedEdges)
(def FromFaceToInnerEdges daedalus/hxDaedalus.iterators.FromFaceToInnerEdges)
(def FromFaceToInnerVertices daedalus/hxDaedalus.iterators.FromFaceToInnerVertices)
(def FromFaceToNeighbourFaces daedalus/hxDaedalus.iterators.FromFaceToNeighbourFaces)
(def FromMeshToFaces daedalus/hxDaedalus.iterators.FromMeshToFaces)
(def FromMeshToVertices daedalus/hxDaedalus.iterators.FromMeshToVertices)
(def FromVertexToHoldingFaces daedalus/hxDaedalus.iterators.FromVertexToHoldingFaces)
(def FromVertexToIncomingEdges daedalus/hxDaedalus.iterators.FromVertexToIncomingEdges)
(def FromVertexToNeighbourVertices daedalus/hxDaedalus.iterators.FromVertexToNeighbourVertices)
(def FromVertexToOutgoingEdges daedalus/hxDaedalus.iterators.FromVertexToOutgoingEdges)

(def SimpleView daedalus/hxDaedalus.view.SimpleView)

;; Define printers, keyword access, etc
(setup-type AStar 'hxDaedalus.ai/AStar [] [:radius])
(setup-type EntityAI 'hxDaedalus.ai/EntityAI [:dirNormY :dirNormX :y :x] [:approximateObject :radius :radiusSquared])
(setup-type Funnel 'hxDaedalus.ai/Funnel [:debugSurface] [:radius])
(setup-type PathFinder 'hxDaedalus.ai/PathFinder [:entity] [:mesh])
(setup-type LinearPathSampler 'hxDaedalus.ai.trajectory/LinearPathSampler [:entity] [:x :y :hasPrev :hasNext :count :countMax :samplingDistance])
(setup-type PathIterator 'hxDaedalus.ai.trajectory/PathIterator [] [:entity :x :y :hasPrev :hasNext :count :countMax])
(setup-type Constants 'hxDaedalus.data/Constants [] [])
(setup-type ConstraintSegment 'hxDaedalus.data/ConstraintSegment [:fromShape] [:id :edges])
(setup-type ConstraintShape 'hxDaedalus.data/ConstraintShape [:segments] [:id])
(setup-type Edge 'hxDaedalus.data/Edge [:fromConstraintSegments :colorDebug] [:id :isReal :isConstrained :originVertex :nextLeftEdge :leftFace :destinationVertex :oppositeEdge :prevLeftEdge :nextRightEdge :prevRightEdge :rotLeftEdge :rotRightEdge :rightFace])
(setup-type Face 'hxDaedalus.data/Face [:colorDebug] [:id :isReal :edge])
(setup-type Mesh 'hxDaedalus.data/Mesh [:_vertices :_edges :_faces] [:height :width :clipping :id :__constraintShapes])
(setup-type Object 'hxDaedalus.data/Object [] [:id :pivotX :pivotY :scaleX :scaleY :rotation :x :y :matrix :coordinates :constraintShape :hasChanged :edges])
(setup-type Vertex 'hxDaedalus.data/Vertex [:colorDebug] [:id :isReal :pos :fromConstraintSegments :edge])
(setup-type Graph 'hxDaedalus.data.graph/Graph [] [:id :edge :node])
(setup-type GraphEdge 'hxDaedalus.data.graph/GraphEdge [] [:id :prev :next :rotPrevEdge :rotNextEdge :oppositeEdge :sourceNode :destinationNode :data])
(setup-type GraphNode 'hxDaedalus.data.graph/GraphNode [] [:id :prev :next :outgoingEdge :successorNodes :data])
(setup-type EdgeData 'hxDaedalus.data.math/EdgeData [:sumDistancesSquared :length :nodesCount] [])
(setup-type Geom2D 'hxDaedalus.data.math/Geom2D [] [])
(setup-type Matrix2D 'hxDaedalus.data.math/Matrix2D [:a :b :c :d :e :f] [])
(setup-type NodeData 'hxDaedalus.data.math/NodeData [:index :point] [])
(setup-type Point2D 'hxDaedalus.data.math/Point2D [:x :y] [:length])
(setup-type Potrace 'hxDaedalus.data.math/Potrace [] [])
(setup-type RandGenerator 'hxDaedalus.data.math/RandGenerator [:rangeMin :rangeMax] [:seed])
(setup-type ShapeSimplifier 'hxDaedalus.data.math/ShapeSimplifier [] [])
(setup-type Tools 'hxDaedalus.data.math/Tools [] [])
(setup-type BitmapMesh 'hxDaedalus.factories/BitmapMesh [] [])
(setup-type BitmapObject 'hxDaedalus.factories/BitmapObject [] [])
(setup-type RectMesh 'hxDaedalus.factories/RectMesh [] [])
(setup-type FromEdgeToRotatedEdges 'hxDaedalus.iterators/FromEdgeToRotatedEdges [] [])
(setup-type FromFaceToInnerEdges 'hxDaedalus.iterators/FromFaceToInnerEdges [] [])
(setup-type FromFaceToInnerVertices 'hxDaedalus.iterators/FromFaceToInnerVertices [] [])
(setup-type FromFaceToNeighbourFaces 'hxDaedalus.iterators/FromFaceToNeighbourFaces [] [])
(setup-type FromMeshToFaces 'hxDaedalus.iterators/FromMeshToFaces [] [])
(setup-type FromMeshToVertices 'hxDaedalus.iterators/FromMeshToVertices [] [])
(setup-type FromVertexToHoldingFaces 'hxDaedalus.iterators/FromVertexToHoldingFaces [] [])
(setup-type FromVertexToIncomingEdges 'hxDaedalus.iterators/FromVertexToIncomingEdges [] [])
(setup-type FromVertexToNeighbourVertices 'hxDaedalus.iterators/FromVertexToNeighbourVertices [] [])
(setup-type FromVertexToOutgoingEdges 'hxDaedalus.iterators/FromVertexToOutgoingEdges [:realEdgesOnly] [])
(setup-type SimpleView 'hxDaedalus.view/SimpleView [:edgesColor :edgesWidth :edgesAlpha :constraintsColor :constraintsWidth :constraintsAlpha :verticesColor :verticesRadius :verticesAlpha :pathsColor :pathsWidth :pathsAlpha :entitiesColor :entitiesWidth :entitiesAlpha :faceColor :faceWidth :faceAlpha] [])

;; Constructor functions, so we can set instance variables in one go
(defn a-star [{:keys [radius mesh], :as opts}] (let [^js obj (AStar.)] (when radius (.set_radius obj radius)) (when mesh (.set_mesh obj mesh)) obj))
(defn entity-ai [{:keys [dirNormY dirNormX y x radius], :as opts}] (let [^js obj (EntityAI.)] (extend-keys! obj opts [:dirNormY :dirNormX :y :x]) (when radius (.set_radius obj radius)) obj))
(defn funnel [{:keys [debugSurface radius], :as opts}] (let [^js obj (Funnel.)] (extend-keys! obj opts [:debugSurface]) (when radius (.set_radius obj radius)) obj))
(defn path-finder [{:keys [entity mesh], :as opts}] (let [^js obj (PathFinder.)] (extend-keys! obj opts [:entity]) (when mesh (.set_mesh obj mesh)) obj))
(defn linear-path-sampler [{:keys [entity count samplingDistance path], :as opts}] (let [^js obj (LinearPathSampler.)] (extend-keys! obj opts [:entity]) (when count (.set_count obj count)) (when samplingDistance (.set_samplingDistance obj samplingDistance)) (when path (.set_path obj path)) obj))
(defn path-iterator [{:keys [entity path], :as opts}] (let [^js obj (PathIterator.)] (when entity (.set_entity obj entity)) (when path (.set_path obj path)) obj))
(defn constants [] (Constants.))
(defn constraint-segment [{:keys [fromShape], :as opts}] (let [^js obj (ConstraintSegment.)] (extend-keys! obj opts [:fromShape]) obj))
(defn constraint-shape [{:keys [segments], :as opts}] (let [^js obj (ConstraintShape.)] (extend-keys! obj opts [:segments]) obj))
(defn edge [{:keys [fromConstraintSegments colorDebug isConstrained originVertex nextLeftEdge leftFace], :as opts}] (let [^js obj (Edge.)] (extend-keys! obj opts [:fromConstraintSegments :colorDebug]) (when isConstrained (.set_isConstrained obj isConstrained)) (when originVertex (.set_originVertex obj originVertex)) (when nextLeftEdge (.set_nextLeftEdge obj nextLeftEdge)) (when leftFace (.set_leftFace obj leftFace)) obj))
(defn face [{:keys [colorDebug], :as opts}] (let [^js obj (Face.)] (extend-keys! obj opts [:colorDebug]) obj))
(defn mesh [{:keys [_vertices _edges _faces clipping], :as opts}] (let [^js obj (Mesh.)] (extend-keys! obj opts [:_vertices :_edges :_faces]) (when clipping (.set_clipping obj clipping)) obj))
(defn object [{:keys [pivotX pivotY scaleX scaleY rotation x y matrix coordinates constraintShape hasChanged], :as opts}] (let [^js obj (Object.)] (when pivotX (.set_pivotX obj pivotX)) (when pivotY (.set_pivotY obj pivotY)) (when scaleX (.set_scaleX obj scaleX)) (when scaleY (.set_scaleY obj scaleY)) (when rotation (.set_rotation obj rotation)) (when x (.set_x obj x)) (when y (.set_y obj y)) (when matrix (.set_matrix obj matrix)) (when coordinates (.set_coordinates obj coordinates)) (when constraintShape (.set_constraintShape obj constraintShape)) (when hasChanged (.set_hasChanged obj hasChanged)) obj))
(defn vertex [{:keys [colorDebug fromConstraintSegments edge], :as opts}] (let [^js obj (Vertex.)] (extend-keys! obj opts [:colorDebug]) (when fromConstraintSegments (.set_fromConstraintSegments obj fromConstraintSegments)) (when edge (.set_edge obj edge)) obj))
(defn graph [] (Graph.))
(defn graph-edge [{:keys [prev next rotPrevEdge rotNextEdge oppositeEdge sourceNode destinationNode data], :as opts}] (let [^js obj (GraphEdge.)] (when prev (.set_prev obj prev)) (when next (.set_next obj next)) (when rotPrevEdge (.set_rotPrevEdge obj rotPrevEdge)) (when rotNextEdge (.set_rotNextEdge obj rotNextEdge)) (when oppositeEdge (.set_oppositeEdge obj oppositeEdge)) (when sourceNode (.set_sourceNode obj sourceNode)) (when destinationNode (.set_destinationNode obj destinationNode)) (when data (.set_data obj data)) obj))
(defn graph-node [{:keys [prev next outgoingEdge successorNodes data], :as opts}] (let [^js obj (GraphNode.)] (when prev (.set_prev obj prev)) (when next (.set_next obj next)) (when outgoingEdge (.set_outgoingEdge obj outgoingEdge)) (when successorNodes (.set_successorNodes obj successorNodes)) (when data (.set_data obj data)) obj))
(defn edge-data [{:keys [sumDistancesSquared length nodesCount], :as opts}] (let [^js obj (EdgeData.)] (extend-keys! obj opts [:sumDistancesSquared :length :nodesCount]) obj))
(defn geom-2d [] (Geom2D.))
(defn matrix-2d [{:keys [a b c d e f], :as opts}] (let [^js obj (Matrix2D.)] (extend-keys! obj opts [:a :b :c :d :e :f]) obj))
(defn node-data [{:keys [index point], :as opts}] (let [^js obj (NodeData.)] (extend-keys! obj opts [:index :point]) obj))
(defn point-2d [{:keys [x y], :as opts}] (let [^js obj (Point2D.)] (extend-keys! obj opts [:x :y]) obj))
(defn potrace [] (Potrace.))
(defn rand-generator [{:keys [rangeMin rangeMax seed], :as opts}] (let [^js obj (RandGenerator.)] (extend-keys! obj opts [:rangeMin :rangeMax]) (when seed (.set_seed obj seed)) obj))
(defn shape-simplifier [] (ShapeSimplifier.))
(defn tools [] (Tools.))
(defn bitmap-mesh [] (BitmapMesh.))
(defn bitmap-object [] (BitmapObject.))
(defn rect-mesh [] (RectMesh.))
(defn from-edge-to-rotated-edges [] (FromEdgeToRotatedEdges.))
(defn from-face-to-inner-edges [{:keys [fromFace], :as opts}] (let [^js obj (FromFaceToInnerEdges.)] (when fromFace (.set_fromFace obj fromFace)) obj))
(defn from-face-to-inner-vertices [{:keys [fromFace], :as opts}] (let [^js obj (FromFaceToInnerVertices.)] (when fromFace (.set_fromFace obj fromFace)) obj))
(defn from-face-to-neighbour-faces [{:keys [fromFace], :as opts}] (let [^js obj (FromFaceToNeighbourFaces.)] (when fromFace (.set_fromFace obj fromFace)) obj))
(defn from-mesh-to-faces [{:keys [fromMesh], :as opts}] (let [^js obj (FromMeshToFaces.)] (when fromMesh (.set_fromMesh obj fromMesh)) obj))
(defn from-mesh-to-vertices [{:keys [fromMesh], :as opts}] (let [^js obj (FromMeshToVertices.)] (when fromMesh (.set_fromMesh obj fromMesh)) obj))
(defn from-vertex-to-holding-faces [{:keys [fromVertex], :as opts}] (let [^js obj (FromVertexToHoldingFaces.)] (when fromVertex (.set_fromVertex obj fromVertex)) obj))
(defn from-vertex-to-incoming-edges [{:keys [fromVertex], :as opts}] (let [^js obj (FromVertexToIncomingEdges.)] (when fromVertex (.set_fromVertex obj fromVertex)) obj))
(defn from-vertex-to-neighbour-vertices [{:keys [fromVertex], :as opts}] (let [^js obj (FromVertexToNeighbourVertices.)] (when fromVertex (.set_fromVertex obj fromVertex)) obj))
(defn from-vertex-to-outgoing-edges [{:keys [realEdgesOnly fromVertex], :as opts}] (let [^js obj (FromVertexToOutgoingEdges.)] (extend-keys! obj opts [:realEdgesOnly]) (when fromVertex (.set_fromVertex obj fromVertex)) obj))
(defn simple-view [{:keys [edgesColor edgesWidth edgesAlpha constraintsColor constraintsWidth constraintsAlpha verticesColor verticesRadius verticesAlpha pathsColor pathsWidth pathsAlpha entitiesColor entitiesWidth entitiesAlpha faceColor faceWidth faceAlpha], :as opts}] (let [^js obj (SimpleView.)] (extend-keys! obj opts [:edgesColor :edgesWidth :edgesAlpha :constraintsColor :constraintsWidth :constraintsAlpha :verticesColor :verticesRadius :verticesAlpha :pathsColor :pathsWidth :pathsAlpha :entitiesColor :entitiesWidth :entitiesAlpha :faceColor :faceWidth :faceAlpha]) obj))

;; Add objects to a mesh with `conj!`
(extend-protocol ITransientCollection
  Mesh
  (-conj! [^js this obj]
    (.insertObject this obj)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Thin functional API on top

(defn build-rect-mesh [w h]
  (.buildRectangle RectMesh 100 100))

(defn rect [x y w h]
  (object {:coordinates (j/lit [0 0 0 h 0 h w h w h w 0 w 0 0 0])
           :x x
           :y y}))

(defn find-path [^js path-finder to-x to-y]
  (let [p #js []]
    (.findPath path-finder to-x to-y p)
    (map vec (partition 2 p))))
