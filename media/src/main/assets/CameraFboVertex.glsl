attribute vec4 aPos;
attribute vec4 aCoordinate;
uniform mat4 uMatrix;
uniform mat4 uCameraMatrix;
varying vec2 vCoordinate;
void main(){
    vCoordinate = (uCameraMatrix * aCoordinate).xy;
    gl_Position = uMatrix * aPos;
}