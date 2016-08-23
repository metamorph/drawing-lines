(ns drawing-lines.core-test
  (:require [clojure.test :refer :all]
            [drawing-lines.core :refer :all]))



(deftest checks-boundaries
  (is (thrown? AssertionError
               (-> (create-canvas 10 10)
                   (draw-pixel [10 10] [1 1 1]))))
  (is (thrown? AssertionError
               (-> (create-canvas 10 10)
                   (draw-pixel [5 5] [0 0 300])))))

(deftest drawing-of-lines
  (is (= [[1 1]
          [2 1]
          [3 2]
          [4 2]
          [5 3]
          [6 3]
          [7 3]
          [8 4]
          [9 4]
          [10 5]
          [11 5]]
         (line-to-pixels [1 1] [11 5])))
  (is (= [[1 1] [2 1] [3 1] [4 1]]
         (line-to-pixels [1 1] [4 1]))))

(deftest rgb-as-int
  (is (= [128 67 234]
         (int->rgb (rgb->int [128 67 234]))))
  (is (= [0 0 0]
         (int->rgb (rgb->int [0 0 0])))))
