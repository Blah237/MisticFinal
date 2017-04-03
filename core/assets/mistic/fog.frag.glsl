#define NX 35
#define NY 35

uniform vec2 fogOrigin;
uniform sampler2D u_texture;
uniform vec2 res;//The width and height of our screen
uniform vec2 dim;
uniform float fogBoard[NX*NY];
uniform vec2 lanterns[10];
uniform vec2 fogReachVec;
uniform float fogReach;
uniform int numLanterns;
uniform int numFireflies;
uniform vec2 gorfPos;
//uniform float thickness;
uniform float leftOffset;
uniform float botOffset;

varying vec2 vTexCoord;

void main() {
    vec2 coord = (gl_FragCoord.xy / res.xy);      // FUTURE NOTE: should get coord in terms of world/map -- convert gl_FragCoord to world space by adding dim/2 and subtracting cameraPos, then divide by world width
    vec2 boardCoord = (gl_FragCoord.xy + vec2(leftOffset, botOffset)) / dim.xy;
    vec2 origin = fogOrigin;
//    coord *= (dim.x/dim.y);
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

    float fogThickness = 1.0-smoothstep(fogReach/float(NX)-.5, fogReach/float(NX), dist);

    vec4 texColor = texture2D(u_texture, vTexCoord);

    vec3 fog = vec3(.5,0.0,.65);


    fogThickness *= fogBoard[cellY*NX + cellX];
//    float fogThickness = fogBoard[cellY*NX + cellX]*thickness;

    for (int i=0; i<10; i++) {
        if (i>=numLanterns) {
            break;
        }

        vec2 lantern = lanterns[i];
        float dx2 = min(abs(coord.x-lantern.x), abs(coord.x + (2.0-lantern.x)));
        dx2 = min(dx2, abs(lantern.x + (2.0-coord.x)));
        float dy2 = min(abs(coord.y-lantern.y), abs(coord.y + (2.0-lantern.y)));
        dy2 = min(dy2, abs(lantern.y + (2.0-coord.y)));

        float dist2 = length(vec2(dx2,dy2));
        float fogThickness2 = smoothstep(.2, .3, dist2);
        fogThickness *= fogThickness2;
    }

    float dx3 = abs(coord.x-.5);
    float dy3 = abs(coord.y-.5);

    float dist3 = length(vec2(dx3, dy3));
    fogThickness *= smoothstep(min(.1 + float(numFireflies)*.05, .4)-.15, min(.1 + float(numFireflies)*.05, .4), dist3);

    fog *= min(1.0,fogThickness);
    gl_FragColor = texColor;
    gl_FragColor.rgb *= max(0.0,1.0-fogThickness);
    gl_FragColor.rgb += fog;
 }
