;
(ns com.example.GLRenderer
  (:gen-class :implements [android.opengl.GLSurfaceView$Renderer]
              :state state
              :init init
              :post-init initRenderer
              :main false)
  (:import  (android.opengl GLES20 GLSurfaceView) (android util.Log) )
)

(set! *warn-on-reflection* true)

(defn -init []
  [[] (atom {:program nil})]
  )

(defn -initRenderer [this]
  (reset! (.state ^com.example.GLRenderer this) {:mAngle (float 0)})
)

(def vertexShaderCode
  "uniform mat4 uMVPMatrix;
   attribute vec4 vPosition;
   void main(){
    gl_Position = uMVPMatrix * vPosition;
  }"
)

(def fragmentShaderCode
  "precision mediump float;
   void main() {
     gl_FragColor = vec4 (0.99671875, 0.99953125, 0.99265625, 1.0);
   }"
)

(defn loadShader [type shaderCode]
  (let [shader (android.opengl.GLES20/glCreateShader type)]
    (do
      (android.opengl.GLES20/glShaderSource shader shaderCode)
      (android.opengl.GLES20/glCompileShader shader)
      shader
    )
  )
)


(defn -onSurfaceCreated [^com.example.GLRenderer this GL10_unused config]
  (do
    (android.opengl.GLES20/glClearColor 0.3 0.3 0.3 1.0)
    (reset! (.state this) (merge @(.state this) {:program  (android.opengl.GLES20/glCreateProgram)}))
    (android.opengl.GLES20/glAttachShader (:program @(.state this)) (loadShader android.opengl.GLES20/GL_VERTEX_SHADER vertexShaderCode ) )
    (android.opengl.GLES20/glAttachShader (:program @(.state this)) (loadShader android.opengl.GLES20/GL_FRAGMENT_SHADER fragmentShaderCode) )
    (android.opengl.GLES20/glLinkProgram (:program @(.state this)))
    (reset! (.state this)
      (merge @(.state this) {:maPositionHandle (android.opengl.GLES20/glGetAttribLocation (:program @(.state this)) "vPosition")})
    )
  )
)

(defn -onSurfaceChanged [^com.example.GLRenderer this GL10_unused width height]
  (let [mProjMatrix (float-array 16) mVMatrix (float-array 16) ratio (float (/ width height))]
    (do
      (android.opengl.GLES20/glViewport 0 0 width height)
      (android.opengl.Matrix/frustumM mProjMatrix 0 (- ratio) ratio -1 1 3 7)
      (android.opengl.Matrix/setLookAtM mVMatrix 0 0 0 -3 0 0 0 0 1.0 0.0)
      (reset! (.state this)
        (merge @(.state this)
          {:mProjMatrix mProjMatrix :mVMatrix mVMatrix :muMVPMatrixHandle (android.opengl.GLES20/glGetUniformLocation (:program @(.state this)) "uMVPMatrix")}
        )
      )
    )
  )
)

(def initShapes
  (let [triangleCoords (into-array Float/TYPE '(-0.5 -0.25 0 0.5 -0.25 0 0.0 0.559016994 0))
        vbb            (java.nio.ByteBuffer/allocateDirect (* 4 9))
        d1             (.order vbb (java.nio.ByteOrder/nativeOrder))
        triangleVertex (.asFloatBuffer vbb)
        d2             (.put ^java.nio.FloatBuffer triangleVertex ^floats triangleCoords)
        d3             (.position triangleVertex 0)
        ]
   triangleVertex
  )
)

(defn -onDrawFrame [^com.example.GLRenderer this GL10_unused]
  (let [mMVPMatrix (float-array 16) mMMatrix (float-array 16)]
    (do
      (android.opengl.GLES20/glClear ^Integer (bit-or android.opengl.GLES20/GL_COLOR_BUFFER_BIT android.opengl.GLES20/GL_DEPTH_BUFFER_BIT))
      (android.opengl.GLES20/glUseProgram ^Integer (:program @(.state this)) )
      (android.opengl.GLES20/glVertexAttribPointer ^Integer (:maPositionHandle @(.state this)) 3 android.opengl.GLES20/GL_FLOAT false 12 ^java.nio.Buffer initShapes)
      (android.opengl.GLES20/glEnableVertexAttribArray  ^Integer (:maPositionHandle @(.state this)))
      (android.opengl.Matrix/multiplyMM ^floats mMVPMatrix 0 ^floats (:mProjMatrix @(.state this)) 0 ^floats (:mVMatrix @(.state this)) 0)
      (android.opengl.Matrix/setRotateM ^floats mMMatrix  0 ^Float (:mAngle @(.state this)) 0.0 0.0 1.0)
      (android.opengl.Matrix/multiplyMM ^floats mMVPMatrix 0 ^floats (:mVMatrix @(.state this)) 0 ^floats mMMatrix 0)
      (android.opengl.Matrix/multiplyMM ^floats mMVPMatrix 0 ^floats (:mProjMatrix @(.state this))  0 ^floats mMVPMatrix  0)
      (android.opengl.GLES20/glUniformMatrix4fv ^Integer (:muMVPMatrixHandle @(.state this)) 1  false ^floats mMVPMatrix 0)
      (android.opengl.GLES20/glDrawArrays  ^Integer android.opengl.GLES20/GL_TRIANGLES 0 3)
    )
  )
)