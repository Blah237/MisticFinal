uniform vec2 res;
uniform float radius;

void main() {
    vec3 glowColor = vec3(1.0, .9, .3);

    vec2 coord = (gl_FragCoord.xy / res.xy);

    float dx = abs(coord.x-.5) * (res.x/res.y);
    float dy = abs(coord.y-.5);

    float D = length(vec2(dx, dy));

    float attenuation = pow(clamp(1.0 - D*D/(radius*radius), 0.0, 1.0), 2.0) * .5;

    gl_FragColor.rgb = glowColor;
    gl_FragColor.a = attenuation;
}
