precision mediump float;

uniform sampler2D u_PagTexture;
uniform sampler2D u_FromTexture;
uniform sampler2D u_ToTexture;
varying vec2 v_TextureCoordinates;

void main() {
    vec4 pagTexture = texture2D(u_PagTexture, v_TextureCoordinates);
    vec4 fromTexture = texture2D(u_FromTexture, v_TextureCoordinates);
    vec4 toTexture = texture2D(u_ToTexture, v_TextureCoordinates);
    gl_FragColor = mix(fromTexture,toTexture,pagTexture.a);
}
