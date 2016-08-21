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
