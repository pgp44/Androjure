(ns com.example.Activity
  (:gen-class :extends android.app.Activity
              :main false
              :exposes-methods {onCreate superOnCreate})
  (:import  [android.util.Log] [android.opengl.GLSurfaceView])
)

(defn -onCreate
  "Called when the activity is initialised."
  [this bundle]
  (do
    (.superOnCreate this bundle)
    (.setContentView this (com.example.GLSurfaceView. this))
  )
)
