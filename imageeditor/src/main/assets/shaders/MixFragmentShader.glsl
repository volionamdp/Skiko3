precision mediump float;

uniform sampler2D u_InputTexture;
uniform sampler2D s_InputTexture;
varying vec2 v_TextureCoordinates;

void main() {
    vec4 color1 = texture2D(s_InputTexture, v_TextureCoordinates);
    vec4 color2 = texture2D(u_InputTexture, v_TextureCoordinates);
    vec4 color3 = vec4(color2.r/color2.a,color2.g/color2.a,color2.b/color2.a,color2.a);
    gl_FragColor = mix(color1,color3,color2.a);
}
