(ns lambdaisland.daedalus.extract-types
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [camel-snake-kebab.core :as csk]))

(defn haxe-files []
  (->> "node_modules/hxdaedalus-js/src/hxDaedalus"
       io/file
       file-seq
       (filter #(str/ends-with? % ".hx"))))

(defn extract-types []
  (for [file (haxe-files)]
    (let [source (slurp file)
          [_ package] (re-find #"package ([^;]+)" source)
          [_ klass] (re-find #"class ([^\s\{]*)" source)
          lines (str/split source #"\R")
          var-lines (filter #(re-find #"public var" %) lines)
          static-lines (filter #(re-find #"public static function" %) lines)
          function-lines (filter #(re-find #"public function" %) lines)
          plain-vars (remove #(re-find #"\(" %) var-lines)
          getters (filter #(re-find #"\(\s*get" %) var-lines)
          setters (filter #(re-find #"set\s*\)" %) var-lines)
          varname #(keyword (second (re-find #"public var\s+(\w+)" %)))
          funname #(second (re-find #"function\s+(\w+)" %))
          constructor-line (some #(when (= "new" (funname %))
                                    %) function-lines)
          constr-args (when (and constructor-line (re-find #"\(\s*\w" constructor-line))
                        (->> (str/split (second (re-find #"\(\s*(.*)\s*\)" constructor-line)) #"\s*,\s*")
                             (map #(re-find #"\w+" %))
                             (map symbol)
                             seq))]
      {:package package
       :klass klass
       :plain-vars (mapv varname plain-vars)
       :getters (mapv varname getters)
       :setters (mapv varname setters)
       :fullname (symbol (str "daedalus/" package "." klass))
       :statics (map funname static-lines)
       :functions (remove #{"new"} (map funname function-lines))
       :constr-args constr-args})))

(defn alias [{:keys [package klass fullname plain-vars getters setters]}]
  `(~'def ~(symbol klass) ~fullname))

(defn setup-type [{:keys [package klass fullname plain-vars getters setters]}]
  `(~'setup-type ~(symbol klass) ~(symbol (str "'hxDaedalus." (str/replace package "hxDaedalus." "") "/" klass)) ~plain-vars ~getters ~setters))

(defn constructor [{:keys [package klass fullname plain-vars getters setters constr-args]}]
  (if (seq (concat plain-vars setters))
    `(~'defn ~(csk/->kebab-case-symbol klass) [~@constr-args ~(array-map :keys `[~@(map (comp symbol name) plain-vars) ~@(map (comp symbol name) setters)] :as 'opts)]
      (~'let [~(symbol "^js") ~'obj (~(symbol (str klass ".")) ~@constr-args)]
       ~@(when (seq plain-vars)
           [`(~'extend-keys! ~'obj ~'opts ~plain-vars)])
       ~@(when (seq setters)
           (for [s setters]
             (list 'when (symbol (name s))
                   (list (symbol (str ".set_" (name s)))
                         'obj
                         (symbol (name s))))))
       ~'obj))
    `(~'defn ~(csk/->kebab-case-symbol klass) [] (~(symbol (str klass "."))))))


(defn print-code-line
  ([args]
   (print-code-line args 0)
   (println))
  ([args depth]
   (if (<= (count (pr-str args)) 90)
     (prn args)
     (let [fcall (pr-str (first args))
           is-vec? (vector? args)]
       (print (if is-vec? "[" "("))
       (print fcall)
       (loop [[arg & args] (next args)
              depth (+ depth
                       (cond
                         is-vec?
                         1
                         (#{"defn" "let"} fcall)
                         2
                         :else
                         (+ 2 (count fcall))))
              line-length 1]
         (if arg
           (if (<= (inc (+ (count (pr-str arg)) line-length)) 90)
             (do
               (print (str " " (pr-str arg)))
               (recur args depth (+ (count (pr-str arg)) line-length)))
             (do
               (println)
               (run! (fn [_] (print " "))
                     (range depth))
               (if (and (< 90 (inc (+ (count (pr-str arg)) depth)))
                        (or (seq? arg) (vector? arg)))
                 (print-code-line arg depth)
                 (print (pr-str arg)))
               (recur args depth (+ line-length (+ depth (count (pr-str arg)))))))
           (print (if is-vec? "]" ")"))))))))

(comment
  (extract-types)


  (run! print-code-line (map setup-type (sort-by :fullname (extract-types))))
  (run! print-code-line (map constructor (sort-by :fullname (extract-types))))
  )



;; => ["( targetCanvas: TargetCanvas )" "targetCanvas: TargetCanvas " "targetCanvas"]
