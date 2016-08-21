# drawing-lines

Weekly Programming Challenge #4: https://medium.com/@jamis/weekly-programming-challenge-4-7fe42f28d5d4#.3pwgoj46j



## Usage


    $ java -jar drawing-lines-0.1.0-standalone.jar [args]

## Options


## Examples

When used as a library, here's how to draw stuff on the canvas, and how to persist the image.

``` clojure
(-> (create-canvas 10 10)
    (draw-pixel [5 5] {:color [10 100 255]})
    (draw pixel [6 6] {:color [10 100 255]})
    (draw-line [2 2] [8 8] {:color [0 0 255]})
    (write-to :ppm "file.ppm"))
```


### Bugs

Probably!

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
