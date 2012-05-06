; All rights available
; --------------------
(ns com.example.GLSurfaceView
  (:gen-class :extends android.opengl.GLSurfaceView
              :main false
              :init init
              :post-init initGl
              :state state)
  (:import  [android.util.Log] [android.opengl.GLSurfaceView])
  )

(def touch-scale-factor (float (/ 180.0 320)))


(defn -init [context]
  [[context] (atom {:renderer nil :mPreviousX 0 :mPreviousY 0})]
)

(defn -initGl [this context]
  (do
    (.setEGLContextClientVersion this 2)
    (reset! (.state this) {:renderer (com.example.GLRenderer.)})
    (.setRenderer this (:renderer @(.state this)))
  )
)

(defn -onTouchEvent [this e]
  ( let [
      x           (.getX e)
      y           (.getY e)
      action      (.getAction e)
      mPreviousX  (:mPreviousX @(.state this))
      mPreviousY  (:mPreviousY @(.state this))
      height      (.getHeight this)
      width       (.getWidth this)
      ]
    (do
      (cond
        (= android.view.MotionEvent/ACTION_MOVE action)
          (let [
            dx1 (- x mPreviousX)
            dy1 (- y mPreviousY)
            dx (if (> y (/ height 2)) (- dx1) dx1 )
            dy (if (< x (/ width 2))  (- dy1) dy1 )
            prevAngle (:mAngle @(.state (:renderer @(.state this))))
            deltaAngle (* touch-scale-factor (+ dx dy))
            newAngle (+ prevAngle deltaAngle)
            dgb (android.util.Log/i "onTouchEvent" "touched")
            ]
            (do
              (reset! (.state (:renderer @(.state this))) (merge @(.state (:renderer @(.state this))) {:mAngle newAngle} ))
              (.requestRender this)
            )
          )
      )
      (reset! (.state this) (merge @(.state this) {:mPreviousX x :mPreviousY y}))
      true
    )
  )
)
