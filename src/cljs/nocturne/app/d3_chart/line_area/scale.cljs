(ns cljs.nocturne.app.d3-chart.line-area.scale
  (:use [cljs.nocturne.app.d3-chart.scale.linear :only [linear-scale]]))

(defn construct-scale
  [x-min x-max y-min y-max {:keys [width height]}]
  [(linear-scale {:domain [x-min x-max]
                  :range-scale [0 width]})
   (linear-scale {:domain [y-min y-max]
                  :range-scale [height 0]})])
