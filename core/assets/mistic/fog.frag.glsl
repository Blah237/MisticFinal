#define NX 35
#define NY 35

uniform vec2 fogOrigin;
uniform sampler2D u_texture;
//uniform vec2 res;//The width and height of our screen
uniform vec2 dim;
uniform float[NX*NY] fogBoard;
//uniform vec2[10] lanterns;
uniform vec2 fogReachVec;
uniform float fogReach;
uniform int numLanterns;
uniform int numFireflies;
uniform vec2 gorfPos;
//uniform float thickness;

varying vec2 vTexCoord;

void main() {
    vec2 coord = gl_FragCoord.xy / dim.xy;      // FUTURE NOTE: should get coord in terms of world/map -- convert gl_FragCoord to world space by adding dim/2 and subtracting cameraPos, then divide by world width
    vec2 origin = fogOrigin / dim.xy;
//    coord *= (dim.x/dim.y);
//    origin *= (dim.x/dim.y);
//    float fogReach = pow(coord.x, 2) / pow(fogReach.x/NX, 2) + pow(coord.y, 2) / pow(fogReach.y/NY, 2);
//    float dist = length((coord-origin)*(dim.x/dim.y));
    float dx = min(abs(coord.x-origin.x), abs(coord.x + (1-origin.x)));       // FUTURE NOTE: upper bound (currently 1) should be the world map width normalized in terms of screen [0.1], i.e if map was 2x width of screen, then upper bound = 2
    dx = min(dx, abs(origin.x + (1-coord.x)));
    float dy = min(abs(coord.y-origin.y), abs(coord.y + (1-origin.y)));
    dy = min(dy, origin.y + abs((1-coord.y)));
//    float theta = atan(dy/dx);
//    float fogReach = (fogReachVec.x*fogReachVec.y) / sqrt(pow(fogReachVec.x*sin(theta), 2) + pow(fogReachVec.y*cos(theta), 2));
    float dist = length(vec2(dx,dy));     // FUTURE NOTE: issue with this -- when wrap around gets closer to the origin, reverts back to original dist even if it was stopped by an obstacle in the middle -- causes the flip in fog curvature
//    float fogThickness = dist/fogReach;                               Possible fix: instead of implementing wrap-around, start a new fog at the wrap around point? This introduces a problem with radius depending on if it's just the fog is tightly curved and only the tip wraps around at first or if the fog has become very flat and/or multiple fog units wrap at once
//    float fogThickness = smoothstep(fogReach, fogReach-.1, dist);      // FUTURE NOTE: (dim.x/NX) should be in terms of map/world coordinates instead, i.e. worldWidht/NX
    int cellX = min(NX-1, int(coord.x*NX));
    int cellY = min(NY-1, int(coord.y*NY));
//    float fogReach = 1;
//    float fogVal = fogBoard[cell];
//    float fogReach = reachBoard[cellY*NX + cellX]/NX;
    float fogThickness = 1-smoothstep(fogReach/NX-.5, fogReach/NX, dist);
//    float fogThickness = max(0,1-dist/fogReach);
//
    vec4 texColor = texture2D(u_texture, vTexCoord);

//    float a = -gl_FragCoord.y;
//    float b = 480/dim.y;
//    float c = a/b;
    vec3 fog = vec3(.5,0,.65);


//    int i = 1;
//    fogThickness *= fogBoard[cellY*NX + cellX];
//    float fogThickness = fogBoard[cellY*NX + cellX]*thickness;

    for (int i=0; i<NX; i++) {
        if (i == cellX) {
            for (int j=0; j<NY; j++) {
                if (j == cellY) {
                    fogThickness *= fogBoard[j*NX + i];
                    break;
                }
            }
        }
    }

//    for (int i=0; i<10; i++) {
//        if (i>=numLanterns) {
//            break;
//        }
//
//        vec2 lantern = lanterns[i];
//        float dx2 = min(abs(coord.x-lantern.x), abs(coord.x + (1-lantern.x)));
//        dx2 = min(dx2, abs(lantern.x + (1-coord.x)));
//        float dy2 = min(abs(coord.y-lantern.y), abs(coord.y + (1-lantern.y)));
//        dy2 = min(dy2, abs(lantern.y + (1-coord.y)));
//
//        float dist2 = length(vec2(dx2,dy2));
//        float fogThickness2 = smoothstep(.1, .2, dist2);
//        fogThickness *= fogThickness2;
//    }

    float dx3 = abs(coord.x-.5);
    float dy3 = abs(coord.y-.5);

    float dist3 = length(vec2(dx3, dy3));
    fogThickness *= smoothstep(min(numFireflies*.05, .3)-.15, min(numFireflies*.05, .3), dist3);

    fog *= min(1,fogThickness);
//    gl_FragColor = vec4(1,0,0,1);
    gl_FragColor = texColor;
    gl_FragColor.rgb *= max(0,1-fogThickness);        // FUTURE NOTE: for firefly light radius, first multiply firefly mask by fogThickness, where 0 (firefly lit) fades out to 1 (full strength fog), then multiply resultant mask in current fashion
//    gl_FragColor.rgba += vec4(fog,max(1,1-fogThickness));
    gl_FragColor.rgb += fog;                       //              fogThickness also needs to be multiplied by the fogBoard(worldCellPos): 0 fog in places with no fog and full fog effect in fogged spaces, i.e. fog is calculated from fog radius
//    gl_FragColor.a *= max(0,1-fogThickness);
//    gl_FragColor.a *= max(0,);
//    gl_FragColor.a *= .2;
//    gl_FragColor = fog;
//      gl_FragColor = texColor;
//    gl_FragColor = texColor*fogVal;
//    gl_FragColor.rgb += vec3(.002*fogVal);
//    gl_FragColor.r = min(1.0, gl_FragColor.r);
//    gl_FragColor.g = min(1.0, gl_FragColor.g);
//    gl_FragColor.b = min(1.0, gl_FragColor.b);
 }
