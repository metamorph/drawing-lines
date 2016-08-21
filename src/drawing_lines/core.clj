(ns drawing-lines.core
  (:gen-class))

;; TODO:
;; - Add javadocs
;; - Add some assertions
;; - Add tests to create the images

(defn create-canvas
  "Creates a canvas with the specified dimensions"
  [width height]
  {:width width
   :height height
   :pixels {}})

(defmulti convert-canvas-to (fn [& args] (first args)))
(defmethod convert-canvas-to :ppm
  [_ {:keys [width height pixels]}]
  (letfn [(color->str [[r g b]]
            (format "%d %d %d" r g b))
          (coord->color [xy]
            (get pixels xy [255 255 255]))
          (line->str [coords]
            (clojure.string/join " " (map (comp color->str coord->color) coords)))]
    (let [headers     ["P3"
                       (format "%d %d" width height)
                       "255"]
          coord-lines (partition-all width (for [y (range height) x (range width)] [x y]))]
      (clojure.string/join "\n" (concat headers
                                        (map line->str coord-lines))))))

(defn write-as
  [canvas format file-name]
  (let [data (convert-canvas-to format canvas)]
    (spit file-name data)))

(defn draw-pixel [canvas
                  xy
                  [red green blue :as color]]
  (update canvas :pixels #(assoc % xy color)))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
