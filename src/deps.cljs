;; Make our library consumable from vanilla-cljs and shadow-cljs
{;; For use with :npm-deps true, or with shadow
 :npm-deps {"hxdaedalus-js" "1.0.1"}
 ;; When not using the npm package, we grab the source directly from the jar,
 ;; cljsjs-style. "global-export" alias allows us to access js/window.hxDaedalus
 ;; as
 ;; (require '["hxdaedalus-js" :as daedalus])
 ;; daedalus/hxDaedalus
 :foreign-libs [{:file "lambdaisland/daedalus/hxDaedalus.js"
                 :provides ["hxdaedalus-js"]
                 ;; The exported module from the package is an object with a
                 ;; hxDaedalus property, when used in the browser this becomes
                 ;; window.hxDaedalus. Basically this is needed to make sure the
                 ;; object access we do in shadow-cljs also works in vanilla.
                 :global-exports {"hxdaedalus-js" hxDaedalus}}]
 ;; Ah yes, the externs. Luckily Haxe is pretty easily "parseable", see
 ;; `extract_types.clj`
 :externs ["lambdaisland/daedalus/hxDaedalus.ext.js"]}
