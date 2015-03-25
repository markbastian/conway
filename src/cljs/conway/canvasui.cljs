(ns conway.canvasui
  (:require [conway.rules :as rules]))

(set! (.-onload js/window)
      nil
      ;(if (and js/document (.-getElementById js/document))
      ;  (set! (.-onkeyup )))
      )
