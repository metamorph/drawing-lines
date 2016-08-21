(ns drawing-lines.core
  (:gen-class))

;; TODO:
;; - Add tests to create the images

(def ^:const max-rgb-value
  "The max allowed RGB value."
  255)

(defn create-canvas
  "Creates a canvas with the specified dimensions"
  [width height]
  {:width width
   :height height
   :pixels {}})

(defmulti convert-canvas-to
  "Converts a canvas to 'data' suitable for the provided format."
  (fn [& args] (first args)))
(defmethod convert-canvas-to :ppm
  [_ {:keys [width height pixels]}]
  (letfn [(color->str [[r g b]] (format "%d %d %d" r g b))
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
  "Write the canvas to file using the specified format."
  [canvas format file-name]
  (let [data (convert-canvas-to format canvas)]
    (spit file-name data)))

(defn draw-pixel
  "Draw a pixel on the canvas."
  [canvas
   [x y :as xy]
   [red green blue :as color]]
  ;; Precondition check for x/y out-of-bound and color values outside of allowed range.
  {:pre [(and (pos? x)
              (pos? y)
              (< x (:width canvas))
              (< y (:height canvas)))
         (every? #(and (pos? %) (<= % max-rgb-value)) color)]}
  (update canvas :pixels #(assoc % xy color)))

(defn line-to-pixels
  "Calculates the pixels to fill when drawing a line from x1y1 to x2y2.
  Uses Bresenham's line algorithm (https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm)."
  [[x1 y1] [x2 y2]]
  nil)

(defn draw-line
  "Draws a line from x1y1 to x2y2 using the specified color"
  [canvas [x1 y1 :as c1] [x2 y2 :as c2] rgb]
  (reduce #(draw-pixel canvas % rgb) canvas (line-to-pixels c1 c2)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
