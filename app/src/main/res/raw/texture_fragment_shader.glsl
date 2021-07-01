#version 300 es
#extension GL_OES_EGL_image_external_essl3 : require

precision mediump float;
      	 				
uniform samplerExternalOES u_TextureUnit;
in vec2 v_TextureCoordinates;
out vec4 outColor;
  
void main() {
    outColor = texture(u_TextureUnit, v_TextureCoordinates);
}