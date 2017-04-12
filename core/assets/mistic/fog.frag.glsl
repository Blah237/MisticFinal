#define NX 31
#define NY 31

uniform vec2 fogOrigin;
uniform sampler2D u_texture;
uniform sampler2D u_texture_perlin;

//uniform sampler2D u_texture_sew;
//uniform sampler2D u_texture_new;
//uniform sampler2D u_texture_nsw;
//uniform sampler2D u_texture_nse;
//uniform sampler2D u_texture_ew;
//uniform sampler2D u_texture_ns;
//uniform sampler2D u_texture_nw;
//uniform sampler2D u_texture_sw;
//uniform sampler2D u_texture_se;
//uniform sampler2D u_texture_ne;
//uniform sampler2D u_texture_w;
//uniform sampler2D u_texture_s;
//uniform sampler2D u_texture_e;
//uniform sampler2D u_texture_n;

uniform vec2 res;//The width and height of our screen
uniform vec2 dim;
uniform float fogBoard[NX*NY];
uniform vec2 lanterns[10];
//uniform vec2 fogReachVec;
//uniform float fogReach;
uniform int numLanterns;
uniform int numFireflies;
uniform vec2 gorfPos;
//uniform float thickness;
uniform float leftOffset;
uniform float botOffset;

uniform float tileW;
uniform float tileH;

varying vec2 vTexCoord;

void main() {
    vec2 coord = (gl_FragCoord.xy / res.xy);      // FUTURE NOTE: should get coord in terms of world/map -- convert gl_FragCoord to world space by adding dim/2 and subtracting cameraPos, then divide by world width
    vec2 boardCoord = gl_FragCoord.xy / dim.xy + vec2(leftOffset, botOffset);
    vec2 origin = fogOrigin;
//    coord.x *= (res.x/res.y);
//    origin *= (dim.x/dim.y);
//    float fogReach = pow(coord.x, 2) / pow(fogReach.x/NX, 2) + pow(coord.y, 2) / pow(fogReach.y/NY, 2);
//    float dist = length((coord-origin)*(dim.x/dim.y));
    float dx = min(abs(coord.x-origin.x), abs(coord.x + (2.0-origin.x)));       // FUTURE NOTE: upper bound (currently 1) should be the world map width normalized in terms of screen [0.1], i.e if map was 2x width of screen, then upper bound = 2
    dx = min(dx, abs(origin.x + (2.0-coord.x)));
    float dy = min(abs(coord.y-origin.y), abs(coord.y + (2.0-origin.y)));
    dy = min(dy, origin.y + abs((2.0-coord.y)));
//    float theta = atan(dy/dx);
//    float fogReach = (fogReachVec.x*fogReachVec.y) / sqrt(pow(fogReachVec.x*sin(theta), 2) + pow(fogReachVec.y*cos(theta), 2));
    float dist = length(vec2(dx,dy));
    int cellX = int(boardCoord.x*float(NX));
    int cellY = int(boardCoord.y*float(NY));

//    float fogThickness = 1.0-smoothstep(fogReach/float(NX)-.5, fogReach/float(NX), dist);

    vec4 texColor = texture2D(u_texture, vTexCoord);

    vec3 fog = vec3(.5,0.0,.65);


//    fogThickness *= fogBoard[cellY*NX + cellX];
//      float fogThickness = fogBoard[cellY*NX + cellX];
    float fogThickness = 0.0;

    if (fogBoard[cellY*NX + cellX] == 1.0) {
        fogThickness = fogBoard[cellY*NX + cellX];
    }

    for (int i=0; i<10; i++) {
        if (i>=numLanterns) {
            break;
        }

        vec2 lantern = lanterns[i];
        float dx2 = abs(coord.x+.02-lantern.x) * (res.x/res.y);
        float dy2 = abs(coord.y-.06-lantern.y);

        float dist2 = length(vec2(dx2,dy2));
        float fogThickness2 = smoothstep(.3, .5, dist2);
        fogThickness *= fogThickness2;
    }

    float dx3 = abs(coord.x-.5) * (res.x/res.y);
    float dy3 = abs(coord.y-.5);

    float dist3 = length(vec2(dx3, dy3));
    fogThickness *= smoothstep(min(.17 + float(numFireflies)*.04, .4)-.15, min(.12 + float(numFireflies)*.04, .4), dist3);

    fogThickness *= min(1.0, texture2D(u_texture_perlin, vTexCoord).a+.4);
    fog *= min(1.0,fogThickness);
    fog *= max(.7, texture2D(u_texture_perlin, vTexCoord).a);

    gl_FragColor = texColor;
//    gl_FragColor = vec4(fog, fogThickness);
    gl_FragColor.rgb *= max(0.0,1.0-fogThickness);
    gl_FragColor.rgb += fog;
//    if (fogThickness > 0) {
//        gl_FragColor.a *= texture2D(u_texture_perlin, vTexCoord).a;
//    }

//    gl_FragColor.a = 0;

//    gl_FragColor = texColor;
//    } else if (fogBoard[cellY*NX + cellX] != 0.0 && fogBoard[cellY*NX + cellX] == 0.9) {
//        if (fogBoard[cellY*NX + cellX] == .1) {
//            texColor = texture2D(u_texture_n, vec2(mod(gl_FragCoord.x, tileW) / tileW, mod(gl_FragCoord.y, tileH) / tileH));
//        } else if (fogBoard[cellY*NX + cellX] == .2) {
//            texColor = texture2D(u_texture_e, vec2(mod(gl_FragCoord.x, tileW) / tileW, mod(gl_FragCoord.y, tileH) / tileH));
//        } else if (fogBoard[cellY*NX + cellX] == .3) {
//            texColor = texture2D(u_texture_s, vec2(mod(gl_FragCoord.x, tileW) / tileW, mod(gl_FragCoord.y, tileH) / tileH));
//        } else if (fogBoard[cellY*NX + cellX] == .4) {
//            texColor = texture2D(u_texture_w, vec2(mod(gl_FragCoord.x, tileW) / tileW, mod(gl_FragCoord.y, tileH) / tileH));
//        } else if (fogBoard[cellY*NX + cellX] == .5) {
//            texColor = texture2D(u_texture_ne, vec2(mod(gl_FragCoord.x, tileW) / tileW, mod(gl_FragCoord.y, tileH) / tileH));
//        } else if (fogBoard[cellY*NX + cellX] == .6) {
//            texColor = texture2D(u_texture_se, vec2(mod(gl_FragCoord.x, tileW) / tileW, mod(gl_FragCoord.y, tileH) / tileH));
//        } else if (fogBoard[cellY*NX + cellX] == .7) {
//            texColor = texture2D(u_texture_sw, vec2(mod(gl_FragCoord.x, tileW) / tileW, mod(gl_FragCoord.y, tileH) / tileH));
//        } else if (fogBoard[cellY*NX + cellX] == .8) {
//            texColor = texture2D(u_texture_nw, vec2(mod(gl_FragCoord.x, tileW) / tileW, mod(gl_FragCoord.y, tileH) / tileH));
//        } else if (fogBoard[cellY*NX + cellX] == .9) {
//            texColor = texture2D(u_texture_ns, vec2(mod(gl_FragCoord.x, tileW) / tileW, mod(gl_FragCoord.y, tileH) / tileH));
//        } else if (fogBoard[cellY*NX + cellX] == .10) {
//            texColor = texture2D(u_texture_ew, vec2(mod(gl_FragCoord.x, tileW) / tileW, mod(gl_FragCoord.y, tileH) / tileH));
//        } else if (fogBoard[cellY*NX + cellX] == .11) {
//            texColor = texture2D(u_texture_new, vec2(mod(gl_FragCoord.x, tileW) / tileW, mod(gl_FragCoord.y, tileH) / tileH));
//        } else if (fogBoard[cellY*NX + cellX] == .12) {
//            texColor = texture2D(u_texture_nse, vec2(mod(gl_FragCoord.x, tileW) / tileW, mod(gl_FragCoord.y, tileH) / tileH));
//        } else if (fogBoard[cellY*NX + cellX] == .13) {
//            texColor = texture2D(u_texture_nsw, vec2(mod(gl_FragCoord.x, tileW) / tileW, mod(gl_FragCoord.y, tileH) / tileH));
//        } else if (fogBoard[cellY*NX + cellX] == .14) {
//            texColor = texture2D(u_texture_sew, vec2(mod(gl_FragCoord.x, tileW) / tileW, mod(gl_FragCoord.y, tileH) / tileH));
//        }
//
//        gl_FragColor = texColor;
//    } else {
//        gl_FragColor = texColor;
//    }
 }
