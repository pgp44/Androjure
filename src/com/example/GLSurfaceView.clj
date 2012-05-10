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

(set! *warn-on-reflection* true)

(def touch-scale-factor (float (/ 180.0 320)))

                                                               `
(defn -init [context]
  [[context] (atom {:renderer nil :mPreviousX 0 :mPreviousY 0})]
)

(defn -initGl [this context]
  (do
    (.setEGLContextClientVersion ^com.example.GLSurfaceView this 2)
    (reset! (.state ^com.example.GLSurfaceView this) {:renderer (com.example.GLRenderer.)})
    (.setRenderer ^com.example.GLSurfaceView this (:renderer @(.state ^com.example.GLSurfaceView this)))
  )
)

(defn -onTouchEvent [^com.example.GLSurfaceView this #^android.view.MotionEvent e]
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
      (android.util.Log/i "onTouchEvent" "enter")
      (cond
        (= android.view.MotionEvent/ACTION_MOVE action)
          (let [
            dx1 (- x mPreviousX)
            dy1 (- y mPreviousY)
            dx (if (> y (/ height 2)) (- dx1) dx1 )
            dy (if (< x (/ width 2))  (- dy1) dy1 )
            dbg (android.util.Log/i "onTouchEvent" "dx and dy calculated")
            prevAngle (:mAngle @(.state ^com.example.GLRenderer (:renderer @(.state ^com.example.GLSurfaceView this))))
            deltaAngle (* touch-scale-factor (+ dx dy))
            newAngle (+ prevAngle deltaAngle)
            dbg2 (android.util.Log/i "onTouchEvent" "newAngle calculated")
            ]
            (do
              (reset! (.state ^com.example.GLRenderer (:renderer @(.state this))) (merge @(.state ^com.example.GLRenderer  (:renderer @(.state this))) {:mAngle newAngle} ))
              (android.util.Log/i "onTouchEvent" "newAngle set")
              (.requestRender ^com.example.GLSurfaceView this)
              (android.util.Log/i "onTouchEvent" "requestRender called")
              )
          )
      )
      (reset! (.state this) (merge @(.state this) {:mPreviousX x :mPreviousY y}))
      (android.util.Log/i "onTouchEvent" "exit")
      true
    )
  )
)
