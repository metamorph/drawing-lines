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
         (every? #(<= 0 % max-rgb-value) color)]}
  (update canvas :pixels #(assoc % xy color)))

(defn- grid-line
  "Calculates the pixels to fill when drawing a line from x1y1 to x2y2.
  Uses Bresenham's line algorithm (https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm).

  This implementation is adapted from http://bpattison.blogspot.se/2010/02/p2.html?m=1
  ;  Copyright (c) Brian Pattison. All rights reserved.
  ;  The use and distribution terms for this software are covered by the
  ;  Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
  ;  which can be found in the file epl-v10.html at the root of this distribution.
  ;  By using this software in any fashion, you are agreeing to be bound by
  ;  the terms of this license.
  ;  You must not remove this notice, or any other, from this software.
  "
  [[x0 y0] [x1 y1]]
  (letfn [(x-major-line [x x0 y0 adx ady x-dir y-dir]
            (if (= x0 x)
              [[x0 y0 0]]
              (let [r (x-major-line (+ x-dir x) x0 y0 adx ady x-dir y-dir)
                    l (last r)]
                (let [[_ y e] l, e (+ e ady)]
                  (if (>= (* e 2) adx)
                    (conj r [x (+ y-dir y) (- e adx)])
                    (conj r [x y e]))))))

          (y-major-line [y x0 y0 adx ady x-dir y-dir]
                        (if (= y0 y)
                          [[x0 y0 0]]
                          (let [r (y-major-line (+ y-dir y) x0 y0 adx ady x-dir y-dir)
                                l (last r)]
                            (let [[x _ e] l, e (+ e adx)]
                              (if (>= (* e 2) ady)
                                (conj r [(+ x-dir x) y (- e ady)])
                                (conj r [x y e]))))))]
    (let [dx  (- x1 x0)
          dy  (- y1 y0)
          m   (if (= dx 0)  ; check for divid by zero
                (* dy java.lang.Double/POSITIVE_INFINITY)
                (/ dy dx))
          adx (Math/abs dx)
          ady (Math/abs dy)
          am  (if (neg? m) (* -1 m) m)]
      (cond
                                        ; q0
        (and (< x0 x1) (<= 0 m 1))
        (x-major-line x1 x0 y0 adx ady -1 +1)
                                        ; q1
        (and (< x0 x1) (and (> 0 m) (>= m -1)))
        (x-major-line x1 x0 y0 adx ady -1 -1)
                                        ; q4
        (and (< x1 x0) (<= 0 m 1))
        (x-major-line x1 x0 y0 adx ady +1 -1)
                                        ; q5
        (and (< x1 x0) (>= 0 m -1))
        (x-major-line x1 x0 y0 adx ady +1 +1)
                                        ; q2
        (and (< y1 y0) (> -1 m))
        (y-major-line y1 x0 y0 adx ady +1 +1)
                                        ; q3
        (and (< y1 y0) (< 1 m))
        (y-major-line y1 x0 y0 adx ady -1 +1)
                                        ; q6
        (and (< y0 y1) (> -1 m))
        (y-major-line y1 x0 y0 adx ady -1 -1)
                                        ; q7
        (and (< y0 y1) (< 1 m))
        (y-major-line y1 x0 y0 adx ady +1 -1)
                                        ; no slope, not a line just a point
                                        ; happens when x0 == x1 and y0 == y1
        true []))))

(defn line-to-pixels [x1y1 x2y2]
  (map (fn [[x y _]] [x y]) (grid-line x1y1 x2y2)))

(defn draw-line
  "Draws a line from x1y1 to x2y2 using the specified color"
  [canvas [x1 y1 :as c1] [x2 y2 :as c2] rgb]
  (reduce #(draw-pixel %1 %2 rgb) canvas (line-to-pixels c1 c2)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
