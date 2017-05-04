#define MAX_LANTERNS 30

uniform vec2 res;

uniform vec2 familiarPos;
uniform vec2 lanternsPos[MAX_LANTERNS];
uniform int numLanterns;
uniform float gorfRadius;

void main() {
    // Familiar Glow
    vec3 familiarGlowColor = vec3(0.7, 1.0, 1.0);

    vec2 familiarDir = familiarPos - (gl_FragCoord.xy / res.xy);
    familiarDir.x *= res.x / res.y;

    float D = length(familiarDir);

    vec3 familiarFalloff = vec3(1.0, 2.0, 60.0);
    float familiarAttenuation = 1.0 / ( familiarFalloff.x + (familiarFalloff.y*D) + (familiarFalloff.z*D*D) );

    gl_FragColor.rgb = familiarGlowColor*familiarAttenuation;
    gl_FragColor.a = familiarAttenuation;
}
