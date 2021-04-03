(ns lambdaisland.daedalus
  (:require ["hxdaedalus-js" :as daedalus]
            [applied-science.js-interop :as j]
            [lambdaisland.data-printers :as data-printers]))

;; Difference between vanilla-cljs and shadow, shadow loads the package, which
;; contains a `hxDaedalus` property. Vanilla-js includes the hxDaedalus.js,
;; which defines hxDaedalus globally, which we then alias to "hxdaedalus-js"
;; required above, via `:global-exports` in `deps.cljs`.
(when-not daedalus/hxDaedalus
  (set! (.-hxDaedalus daedalus) daedalus)
  (set! (.-hxPixels daedalus) js/hxPixels))

(defn to-edn [plain-vars getters]
  (let [vars (remove #(= "_" (first (name %)))
                     (concat plain-vars getters))]
    (fn [obj]
      (into {} (map (juxt identity #(try
                                      (get obj %)
                                      (catch :default e
                                        ::error)))) vars))))

(defn setup-type [type tag plain-vars getters setters]
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
        getters (set getters)
        setters (set setters)]
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
      ITransientAssociative
      (-assoc! [this k v]
        (cond
          (contains? plain-vars k)
          (j/assoc! this k v)
          (contains? setters k)
          (.call ^js (j/get this (str "set_" (name k))) this v)))
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
(setup-type AStar 'hxDaedalus.ai/AStar [] [:radius] [:radius :mesh])
(setup-type EntityAI 'hxDaedalus.ai/EntityAI [:dirNormY :dirNormX :y :x]
            [:approximateObject :radius :radiusSquared]
            [:radius])
(setup-type Funnel 'hxDaedalus.ai/Funnel [:debugSurface] [:radius] [:radius])
(setup-type PathFinder 'hxDaedalus.ai/PathFinder [:entity] [:mesh] [:mesh])
(setup-type LinearPathSampler 'hxDaedalus.ai.trajectory/LinearPathSampler [:entity]
            [:x :y :hasPrev :hasNext :count :countMax :samplingDistance]
            [:count :samplingDistance :path])
(setup-type PathIterator 'hxDaedalus.ai.trajectory/PathIterator []
            [:entity :x :y :hasPrev :hasNext :count :countMax]
            [:entity :path])
(setup-type Constants 'hxDaedalus.data/Constants [] [] [])
(setup-type ConstraintSegment 'hxDaedalus.data/ConstraintSegment [:fromShape] [:id :edges] [])
(setup-type ConstraintShape 'hxDaedalus.data/ConstraintShape [:segments] [:id] [])
(setup-type Edge 'hxDaedalus.data/Edge [:fromConstraintSegments :colorDebug]
            [:id :isReal :isConstrained :originVertex :nextLeftEdge :leftFace :destinationVertex :oppositeEdge :prevLeftEdge :nextRightEdge :prevRightEdge :rotLeftEdge :rotRightEdge :rightFace]
            [:isConstrained :originVertex :nextLeftEdge :leftFace])
(setup-type Face 'hxDaedalus.data/Face [:colorDebug] [:id :isReal :edge] [])
(setup-type Mesh 'hxDaedalus.data/Mesh [:_vertices :_edges :_faces]
            [:height :width :clipping :id :__constraintShapes]
            [:clipping])
(setup-type Object 'hxDaedalus.data/Object []
            [:id :pivotX :pivotY :scaleX :scaleY :rotation :x :y :matrix :coordinates :constraintShape :hasChanged :edges]
            [:pivotX :pivotY :scaleX :scaleY :rotation :x :y :matrix :coordinates :constraintShape :hasChanged])
(setup-type Vertex 'hxDaedalus.data/Vertex [:colorDebug]
            [:id :isReal :pos :fromConstraintSegments :edge]
            [:fromConstraintSegments :edge])
(setup-type Graph 'hxDaedalus.data.graph/Graph [] [:id :edge :node] [])
(setup-type GraphEdge 'hxDaedalus.data.graph/GraphEdge []
            [:id :prev :next :rotPrevEdge :rotNextEdge :oppositeEdge :sourceNode :destinationNode :data]
            [:prev :next :rotPrevEdge :rotNextEdge :oppositeEdge :sourceNode :destinationNode :data])
(setup-type GraphNode 'hxDaedalus.data.graph/GraphNode []
            [:id :prev :next :outgoingEdge :successorNodes :data]
            [:prev :next :outgoingEdge :successorNodes :data])
(setup-type EdgeData 'hxDaedalus.data.math/EdgeData
            [:sumDistancesSquared :length :nodesCount]
            []
            [])
(setup-type Geom2D 'hxDaedalus.data.math/Geom2D [] [] [])
(setup-type Matrix2D 'hxDaedalus.data.math/Matrix2D [:a :b :c :d :e :f] [] [])
(setup-type NodeData 'hxDaedalus.data.math/NodeData [:index :point] [] [])
(setup-type Point2D 'hxDaedalus.data.math/Point2D [:x :y] [:length] [])
(setup-type Potrace 'hxDaedalus.data.math/Potrace [] [] [])
(setup-type RandGenerator 'hxDaedalus.data.math/RandGenerator [:rangeMin :rangeMax] [:seed]
            [:seed])
(setup-type ShapeSimplifier 'hxDaedalus.data.math/ShapeSimplifier [] [] [])
(setup-type Tools 'hxDaedalus.data.math/Tools [] [] [])
(setup-type BitmapMesh 'hxDaedalus.factories/BitmapMesh [] [] [])
(setup-type BitmapObject 'hxDaedalus.factories/BitmapObject [] [] [])
(setup-type RectMesh 'hxDaedalus.factories/RectMesh [] [] [])
(setup-type FromEdgeToRotatedEdges 'hxDaedalus.iterators/FromEdgeToRotatedEdges [] [] [])
(setup-type FromFaceToInnerEdges 'hxDaedalus.iterators/FromFaceToInnerEdges [] [] [:fromFace])
(setup-type FromFaceToInnerVertices 'hxDaedalus.iterators/FromFaceToInnerVertices [] []
            [:fromFace])
(setup-type FromFaceToNeighbourFaces 'hxDaedalus.iterators/FromFaceToNeighbourFaces [] []
            [:fromFace])
(setup-type FromMeshToFaces 'hxDaedalus.iterators/FromMeshToFaces [] [] [:fromMesh])
(setup-type FromMeshToVertices 'hxDaedalus.iterators/FromMeshToVertices [] [] [:fromMesh])
(setup-type FromVertexToHoldingFaces 'hxDaedalus.iterators/FromVertexToHoldingFaces [] []
            [:fromVertex])
(setup-type FromVertexToIncomingEdges 'hxDaedalus.iterators/FromVertexToIncomingEdges [] []
            [:fromVertex])
(setup-type FromVertexToNeighbourVertices
            'hxDaedalus.iterators/FromVertexToNeighbourVertices
            []
            []
            [:fromVertex])
(setup-type FromVertexToOutgoingEdges 'hxDaedalus.iterators/FromVertexToOutgoingEdges
            [:realEdgesOnly]
            []
            [:fromVertex])
(setup-type SimpleView 'hxDaedalus.view/SimpleView
            [:edgesColor :edgesWidth :edgesAlpha :constraintsColor :constraintsWidth :constraintsAlpha :verticesColor :verticesRadius :verticesAlpha :pathsColor :pathsWidth :pathsAlpha :entitiesColor :entitiesWidth :entitiesAlpha :faceColor :faceWidth :faceAlpha]
            []
            [])

;; Constructor functions, so we can set instance variables in one go
(defn a-star [{:keys [radius mesh], :as opts}]
  (let [^js obj (AStar.)]
    (when radius (.set_radius obj radius)) (when mesh (.set_mesh obj mesh))
    obj))

(defn entity-ai [{:keys [dirNormY dirNormX y x radius], :as opts}]
  (let [^js obj (EntityAI.)]
    (extend-keys! obj opts [:dirNormY :dirNormX :y :x])
    (when radius (.set_radius obj radius))
    obj))

(defn funnel [{:keys [debugSurface radius], :as opts}]
  (let [^js obj (Funnel.)]
    (extend-keys! obj opts [:debugSurface])
    (when radius (.set_radius obj radius))
    obj))

(defn path-finder [{:keys [entity mesh], :as opts}]
  (let [^js obj (PathFinder.)]
    (extend-keys! obj opts [:entity]) (when mesh (.set_mesh obj mesh))
    obj))

(defn linear-path-sampler [{:keys [entity count samplingDistance path], :as opts}]
  (let [^js obj (LinearPathSampler.)]
    (extend-keys! obj opts [:entity])
    (when count (.set_count obj count))
    (when samplingDistance (.set_samplingDistance obj samplingDistance))
    (when path (.set_path obj path))
    obj))

(defn path-iterator [{:keys [entity path], :as opts}]
  (let [^js obj (PathIterator.)]
    (when entity (.set_entity obj entity))
    (when path (.set_path obj path))
    obj))

(defn constants [] (Constants.))

(defn constraint-segment [{:keys [fromShape], :as opts}]
  (let [^js obj (ConstraintSegment.)]
    (extend-keys! obj opts [:fromShape])
    obj))

(defn constraint-shape [{:keys [segments], :as opts}]
  (let [^js obj (ConstraintShape.)]
    (extend-keys! obj opts [:segments])
    obj))

(defn edge
  [{:keys [fromConstraintSegments colorDebug isConstrained originVertex nextLeftEdge leftFace], :as opts}]
  (let [^js obj (Edge.)]
    (extend-keys! obj opts [:fromConstraintSegments :colorDebug])
    (when isConstrained (.set_isConstrained obj isConstrained))
    (when originVertex (.set_originVertex obj originVertex))
    (when nextLeftEdge (.set_nextLeftEdge obj nextLeftEdge))
    (when leftFace (.set_leftFace obj leftFace))
    obj))

(defn face [{:keys [colorDebug], :as opts}]
  (let [^js obj (Face.)]
    (extend-keys! obj opts [:colorDebug])
    obj))

(defn mesh [width height {:keys [_vertices _edges _faces clipping], :as opts}]
  (let [^js obj (Mesh. width height)]
    (extend-keys! obj opts [:_vertices :_edges :_faces])
    (when clipping (.set_clipping obj clipping))
    obj))

(defn object
  [{:keys [pivotX pivotY scaleX scaleY rotation x y matrix coordinates constraintShape hasChanged], :as opts}]
  (let [^js obj (Object.)]
    (when pivotX (.set_pivotX obj pivotX))
    (when pivotY (.set_pivotY obj pivotY))
    (when scaleX (.set_scaleX obj scaleX))
    (when scaleY (.set_scaleY obj scaleY))
    (when rotation (.set_rotation obj rotation))
    (when x (.set_x obj x))
    (when y (.set_y obj y))
    (when matrix (.set_matrix obj matrix))
    (when coordinates (.set_coordinates obj coordinates))
    (when constraintShape (.set_constraintShape obj constraintShape))
    (when hasChanged (.set_hasChanged obj hasChanged))
    obj))

(defn vertex [{:keys [colorDebug fromConstraintSegments edge], :as opts}]
  (let [^js obj (Vertex.)]
    (extend-keys! obj opts [:colorDebug])
    (when fromConstraintSegments (.set_fromConstraintSegments obj fromConstraintSegments))
    (when edge (.set_edge obj edge))
    obj))

(defn graph [] (Graph.))

(defn graph-edge
  [{:keys [prev next rotPrevEdge rotNextEdge oppositeEdge sourceNode destinationNode data], :as opts}]
  (let [^js obj (GraphEdge.)]
    (when prev (.set_prev obj prev)) (when next (.set_next obj next))
    (when rotPrevEdge (.set_rotPrevEdge obj rotPrevEdge))
    (when rotNextEdge (.set_rotNextEdge obj rotNextEdge))
    (when oppositeEdge (.set_oppositeEdge obj oppositeEdge))
    (when sourceNode (.set_sourceNode obj sourceNode))
    (when destinationNode (.set_destinationNode obj destinationNode))
    (when data (.set_data obj data))
    obj))

(defn graph-node [{:keys [prev next outgoingEdge successorNodes data], :as opts}]
  (let [^js obj (GraphNode.)]
    (when prev (.set_prev obj prev)) (when next (.set_next obj next))
    (when outgoingEdge (.set_outgoingEdge obj outgoingEdge))
    (when successorNodes (.set_successorNodes obj successorNodes))
    (when data (.set_data obj data))
    obj))

(defn edge-data [{:keys [sumDistancesSquared length nodesCount], :as opts}]
  (let [^js obj (EdgeData.)]
    (extend-keys! obj opts [:sumDistancesSquared :length :nodesCount])
    obj))

(defn geom-2-d [] (Geom2D.))

(defn matrix-2-d [a_ b_ c_ d_ e_ f_ {:keys [a b c d e f], :as opts}]
  (let [^js obj (Matrix2D. a_ b_ c_ d_ e_ f_)]
    (extend-keys! obj opts [:a :b :c :d :e :f])
    obj))

(defn node-data [{:keys [index point], :as opts}]
  (let [^js obj (NodeData.)]
    (extend-keys! obj opts [:index :point])
    obj))

(defn point-2-d [x_ y_ {:keys [x y], :as opts}]
  (let [^js obj (Point2D. x_ y_)]
    (extend-keys! obj opts [:x :y])
    obj))

(defn potrace [] (Potrace.))

(defn rand-generator [seed rangeMin_ rangeMax_ {:keys [rangeMin rangeMax seed], :as opts}]
  (let [^js obj (RandGenerator. seed rangeMin_ rangeMax_)]
    (extend-keys! obj opts [:rangeMin :rangeMax])
    (when seed (.set_seed obj seed))
    obj))

(defn shape-simplifier [] (ShapeSimplifier.))
(defn tools [] (Tools.))
(defn bitmap-mesh [] (BitmapMesh.))
(defn bitmap-object [] (BitmapObject.))
(defn rect-mesh [] (RectMesh.))
(defn from-edge-to-rotated-edges [] (FromEdgeToRotatedEdges.))

(defn from-face-to-inner-edges [{:keys [fromFace], :as opts}]
  (let [^js obj (FromFaceToInnerEdges.)]
    (when fromFace (.set_fromFace obj fromFace)) obj))

(defn from-face-to-inner-vertices [{:keys [fromFace], :as opts}]
  (let [^js obj (FromFaceToInnerVertices.)]
    (when fromFace (.set_fromFace obj fromFace))
    obj))

(defn from-face-to-neighbour-faces [{:keys [fromFace], :as opts}]
  (let [^js obj (FromFaceToNeighbourFaces.)]
    (when fromFace (.set_fromFace obj fromFace))
    obj))

(defn from-mesh-to-faces [{:keys [fromMesh], :as opts}]
  (let [^js obj (FromMeshToFaces.)]
    (when fromMesh (.set_fromMesh obj fromMesh))
    obj))

(defn from-mesh-to-vertices [{:keys [fromMesh], :as opts}]
  (let [^js obj (FromMeshToVertices.)]
    (when fromMesh (.set_fromMesh obj fromMesh))
    obj))

(defn from-vertex-to-holding-faces [{:keys [fromVertex], :as opts}]
  (let [^js obj (FromVertexToHoldingFaces.)]
    (when fromVertex (.set_fromVertex obj fromVertex))
    obj))

(defn from-vertex-to-incoming-edges [{:keys [fromVertex], :as opts}]
  (let [^js obj (FromVertexToIncomingEdges.)]
    (when fromVertex (.set_fromVertex obj fromVertex))
    obj))

(defn from-vertex-to-neighbour-vertices [{:keys [fromVertex], :as opts}]
  (let [^js obj (FromVertexToNeighbourVertices.)]
    (when fromVertex (.set_fromVertex obj fromVertex))
    obj))

(defn from-vertex-to-outgoing-edges [{:keys [realEdgesOnly fromVertex], :as opts}]
  (let [^js obj (FromVertexToOutgoingEdges.)]
    (extend-keys! obj opts [:realEdgesOnly])
    (when fromVertex (.set_fromVertex obj fromVertex))
    obj))

(defn simple-view
  [targetCanvas {:keys [edgesColor edgesWidth edgesAlpha constraintsColor constraintsWidth
                        constraintsAlpha verticesColor verticesRadius verticesAlpha pathsColor
                        pathsWidth pathsAlpha entitiesColor entitiesWidth entitiesAlpha
                        faceColor faceWidth faceAlpha], :as opts}]
  (let [^js obj (SimpleView. targetCanvas)]
    (extend-keys! obj opts
                  [:edgesColor :edgesWidth :edgesAlpha :constraintsColor :constraintsWidth
                   :constraintsAlpha :verticesColor :verticesRadius :verticesAlpha :pathsColor
                   :pathsWidth :pathsAlpha :entitiesColor :entitiesWidth :entitiesAlpha
                   :faceColor :faceWidth :faceAlpha])
    obj))

;; Add objects to a mesh with `conj!`
(extend-protocol ITransientCollection
  Mesh
  (-conj! [^js this obj]
    (.insertObject this obj)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Thin wrapper API

(defn build-rect-mesh [w h]
  (.buildRectangle RectMesh w h))

(defn rect [x y w h]
  (object {:coordinates (j/lit [0 0 0 h 0 h w h w h w 0 w 0 0 0])
           :x x
           :y y}))

(defn polygon
  "Construct a polygon from a sequence of [x y] coordinate pairs. Can be `conj!`ed
  onto a mesh."
  [coords]
  (let [segments (partition 2 1 (cons (last coords) coords))]
    (object {:coordinates
             (let [arr #js []]
               (doseq [[[x1 y1] [x2 y2]] segments]
                 (j/push! arr x1)
                 (j/push! arr y1)
                 (j/push! arr x2)
                 (j/push! arr y2))
               arr)})))

(defn find-path
  "Uses the path-finder to find a path from the path-finder's entity's current
  position to the destination. Will fill up and return an array, which can be
  reused, or shared with a LinearPathsampler."
  ([^js path-finder to-x to-y]
   (find-path path-finder to-x to-y #js []))
  ([^js path-finder to-x to-y path]
   (.findPath path-finder to-x to-y path)
   path))

(defn pairs
  "The path finder returns a flat list of x and y values, turn this into [x y]
  pairs for easier processing."
  [path]
  (map vec (partition 2 path)))

(defn reset
  "Reset a linear-path-sampler or path-iterator"
  [^js sampler-or-iterator]
  (.reset sampler-or-iterator))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Bitmap

(defn img->image-data
  "Return an ImageData for a given <img> tag, using an in-memory canvas"
  [img]
  (let [^js canvas (js/document.createElement "canvas")
        ^js context (.getContext canvas "2d")
        width (j/get img :width)
        height (j/get img :height)]
    (j/assoc! canvas :width width :height height)
    (.drawImage context img 0 0)
    (.getImageData context 0 0 width height)))

(defn image-data->bmp-data [image-data]
  (daedalus/hxPixels._Pixels.Pixels_Impl_.fromImageData image-data))

(defn bmp-data->object [bmp-data]
  (.buildFromBmpData ^js BitmapObject bmp-data))

(defn img->object
  "Convert a black-and-white bitmap image into an object that you can `conj!` onto
  your mesh. Takes a HTMLImageElement."
  [img-element]
  (bmp-data->object
   (image-data->bmp-data
    (img->image-data img-element))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Debug view

(defn draw-mesh [^js view world]
  (.drawMesh view world))

(defn draw-entity [^js view entity]
  (.drawEntity view entity))

(defn draw-path [^js view path]
  (let [path (if (array? path)
               path
               (into-array (mapcat identity path)))]
    (.drawPath view path)))

(defn clear [^js view]
  (.clear (j/get view :graphics)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Combined path finder/sampler with a more convenient API

(defprotocol IPathHandler
  (set-location [this x y]
    "Move the entity to a new location, this will clear out the current path.
    Use this instead of setting :x/:y on the entity directly, to prevent the
    path sampler from overwriting them on the next iteration.")
  (set-destination [this x y]
    "Set the destination for the entity, clearing out the current path.")
  (set-mesh [this mesh]
    "Replace the mesh, this will clear the current path.")
  (next! [this]
    "Move the entity one step closer to its destination. Updates entity.x /
    entity.y, returns nil."))

(defn path-handler
  "Combined path-finder / path-sampler, hiding a bunch of implementation details,
  and helping to keep these different stateful objects in sync.
  See [[IPathHandler]] for method definitions."
  [{:keys [entity mesh sampling-distance]
    :or {sampling-distance 5}}]
  (let [path (j/lit [(.-x entity) (.-y entity)])
        finder (path-finder {:entity entity :mesh mesh})
        sampler (linear-path-sampler {:entity entity
                                      :samplingDistance sampling-distance
                                      :path path})]
    (reify IPathHandler
      (set-location [this x y]
        (j/assoc! entity :x x :y y)
        (j/assoc! sampler :_currentX x :_currentY y)
        (.splice path 0 (.-length path))
        (.push path x)
        (.push path y))
      (set-destination [this x y]
        (find-path finder x y path))
      (set-mesh [this mesh]
        (.set_mesh path-finder mesh)
        (set-location this (.-x entity) (.-y entity)))
      (next! [this]
        (.next sampler)
        nil))))
