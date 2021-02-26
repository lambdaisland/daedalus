(ns lambdaisland.daedalus.extract-types
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [camel-snake-kebab.core :as csk]))

(def files (->> "node_modules/hxdaedalus-js/src/hxDaedalus"
                io/file
                file-seq
                (filter #(str/ends-with? % ".hx"))))

(spit "resources/daedalus_types.edn"
      (pr-str
       (for [file files]
         (let [source (slurp file)
               [_ package] (re-find #"package ([^;]+)" source)
               [_ klass] (re-find #"class ([^\s\{]*)" source)
               var-lines (filter #(re-find #"public var" %) (str/split source #"\R"))
               plain-vars (remove #(re-find #"\(" %) var-lines)
               getters (filter #(re-find #"\(get" %) var-lines)
               varname #(second (re-find #"public var\s+(\w+)" %))]
           [package klass (map varname plain-vars) (map varname getters)]))))

(run! prn
      (for [{:keys [package klass fullname plain-vars getters setters]}
            (sort-by :fullname
                     (for [file files]
                       (let [source (slurp file)
                             [_ package] (re-find #"package ([^;]+)" source)
                             [_ klass] (re-find #"class ([^\s\{]*)" source)
                             var-lines (filter #(re-find #"public var" %) (str/split source #"\R"))
                             plain-vars (remove #(re-find #"\(" %) var-lines)
                             getters (filter #(re-find #"\(\s*get" %) var-lines)
                             setters (filter #(re-find #"set\s*\)" %) var-lines)
                             varname #(keyword (second (re-find #"public var\s+(\w+)" %)))]
                         {:package package
                          :klass klass
                          :plain-vars (mapv varname plain-vars)
                          :getters (mapv varname getters)
                          :setters (mapv varname setters)
                          :fullname (symbol (str "daedalus/" package "." klass))})))]
        #_`(~'def ~(symbol klass) ~fullname)
        #_`(~'setup-type ~(symbol klass) ~(symbol (str "'daedalus." (str/replace package "hxDaedalus." "") "/" klass)) ~plain-vars ~getters #_~setters)
        (if (seq (concat plain-vars setters))
          `(~'defn ~(csk/->kebab-case-symbol klass) [~(array-map :keys `[~@(map (comp symbol name) plain-vars) ~@(map (comp symbol name) setters)] :as 'opts)]
            (~'let [~(symbol "^js") ~'obj (~(symbol (str klass ".")))]
             ~@(when (seq plain-vars)
                 [`(~'extend-keys! ~'obj ~'opts ~plain-vars)])
             ~@(when (seq setters)
                 (for [s setters]
                   (list 'when (symbol (name s))
                         (list (symbol (str ".set_" (name s)))
                               'obj
                               (symbol (name s))))))
             ~'obj))
          `(~'defn ~(csk/->kebab-case-symbol klass) [] (~(symbol (str klass ".")))))
        ))
