(ns lambdaisland.daedalus.canvas-view
  "Visualize your mesh and paths for easy debugging"
  (:require ["hxdaedalus-js" :as daedalus]
            [lambdaisland.daedalus :as dae]
            [applied-science.js-interop :as j]))

;; Sadly the hxDaedalus-js build is borked, it was built with a missing
;; compile-time constant, so the JS code contains `null.split(...)`. So we redo
;; the constructor, then copy over the prototype.
(defn BasicCanvas [{:keys [width height framerate background-color]
                    :or {width 600
                         height 400
                         framerate 60
                         background-color "#FFFFFF"}}]
  (this-as this
    (let [^js canvas (js/window.document.createElement "canvas")
          ^js style (j/get canvas :style)
          ^js surface (.getContext canvas "2d" nil)
          ^js css (js/window.document.createElement "style")]
      (j/assoc! canvas
                :width width
                :height height)
      (j/assoc! this
                :canvas canvas
                :dom canvas
                :image canvas
                :body js/window.document.body
                :surface surface
                :style style
                :header (j/lit {:width width
                                :height height
                                :frameRate framerate
                                :bgColor background-color}))
      (j/assoc! style
                :paddingLeft "0px"
                :paddingTop "0px"
                :left "0px"
                :right "0px"
                :position "absolute"
                :backgroundColor background-color)
      (j/assoc! css
                :innerHTML "@keyframes spin { from { transform:rotate( 0deg ); } to { transform:rotate( 360deg ); } }"
                :animation "spin 1s linear infinite")
      (.appendChild ^js (first (js/window.document.getElementsByTagName "head"))
                    css)
      (.loop this framerate)
      (.appendChild ^js js/window.document.body canvas)
      this)))

(j/extend! (.-prototype BasicCanvas)
           (.-prototype (if daedalus/wings
                          daedalus/wings.jsCanvas.BasicCanvas
                          js/wings.jsCanvas.BasicCanvas)))

(defn canvas-view
  ([]
   (canvas-view {} {}))
  ([canvas-opts view-opts]
   (dae/simple-view (BasicCanvas. canvas-opts view-opts) view-opts)))
