#define MAX_LANTERNS 30

uniform vec2 res;

uniform vec2 lanternsPos[MAX_LANTERNS];
uniform int numLanterns;

void main() {
    // Lantern Glows
    vec3 lanternGlowColor = vec3(1.0, .9, .3);
    float lanternAttenuations[MAX_LANTERNS];

    for (int i=0; i<MAX_LANTERNS; i++) {
        if (i>=numLanterns) {
            break;
        }
        vec2 lanternDir = vec2(lanternsPos[i].x-.004, lanternsPos[i].y+.17) - (gl_FragCoord.xy / res.xy);
        lanternDir.x *= res.x / res.y;

        float D = length(lanternDir);
        float radius = .6;
        lanternAttenuations[i] = pow(clamp(1.0 - D*D/(radius*radius), 0.0, 1.0), 2) * .8;
    }

    gl_FragColor = vec4(lanternGlowColor, 0.0);
    for (int i=0; i<MAX_LANTERNS; i++) {
        if (i>=numLanterns) {
            break;
        }
        gl_FragColor.a = max(gl_FragColor.a, lanternAttenuations[i]);
    }
}
