(ns walkthrough
  (:require ["hxdaedalus-js" :as daedalus]
            [lambdaisland.daedalus :as dae]
            [lambdaisland.daedalus.canvas-view :as view]
            [applied-science.js-interop :as j]))

(def view (view/canvas-view))

(def entity (dae/entity-ai {:x 10 :y 10 :radius 10}))

(def mesh (dae/build-rect-mesh 3965 1000))
(def path-finder (dae/path-finder {:entity entity :mesh mesh}))

;; add obstacle
#_(conj! mesh (dae/rect 10 12 10 10))

;; find a path
(def path (dae/find-path path-finder 30 30))

(view/draw-mesh view mesh)
(view/draw-entity view entity)
(view/draw-path view path)

(def img (first (js/document.getElementsByTagName "img")))


(conj! mesh (dae/bmp-data->object
             (dae/image-data->bmp-data
              (dae/img->image-data img))))

(def path (dae/find-path path-finder 500 300))

(view/draw-path view path)

(def path (into-array (mapcat identity path)))

(def e #js {:x 0 :y 0})

(def sampler (dae/linear-path-sampler {:entity e
                                       :samplingDistance 5
                                       :path path}))

(do
  (.next sampler)
  (view/clear view)
  (view/draw-mesh view mesh)
  (view/draw-entity view entity)
  (view/draw-path view path)


  )
