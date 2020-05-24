precision mediump float;
uniform sampler2D uSampler;
varying vec2 vCoordinate;
uniform vec2 uStep;
uniform float uBeautyLevel;
vec2 blurCoordinates[12];
const highp vec3 W = vec3(0.299,0.587,0.114);
const mat3 saturateMatrix = mat3(
		1.1102,-0.0598,-0.061,
		-0.0774,1.0826,-0.1186,
		-0.0228,-0.0228,1.1772);

vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

float hardLight(float color) {
	if(color <= 0.5) {
		color = color * color * 2.0;
	} else {
		color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);
	}
	return color;
}

void main(){
    vec4 sourceColor = texture2D(uSampler, vCoordinate);

    if (uBeautyLevel == 1.0) {
        gl_FragColor = sourceColor;
        return;
    }

    blurCoordinates[0] = vCoordinate + uStep * vec2(5.0,-8.0);
    blurCoordinates[1] = vCoordinate + uStep * vec2(5.0,8.0);
    blurCoordinates[2] = vCoordinate + uStep * vec2(-5.0,-8.0);
    blurCoordinates[3] = vCoordinate + uStep * vec2(-5.0,8.0);

    blurCoordinates[4] = vCoordinate + uStep * vec2(8.0,-5.0);
    blurCoordinates[5] = vCoordinate + uStep * vec2(8.0,5.0);
    blurCoordinates[6] = vCoordinate + uStep * vec2(-8.0,-5.0);
    blurCoordinates[7] = vCoordinate + uStep * vec2(-8.0,5.0);

    blurCoordinates[8] = vCoordinate + uStep * vec2(-4.0,-4.0);
    blurCoordinates[9] = vCoordinate + uStep * vec2(-4.0,4.0);
    blurCoordinates[10] = vCoordinate + uStep * vec2(4.0,-4.0);
    blurCoordinates[11] = vCoordinate + uStep * vec2(4.0,4.0);

    vec3 sumColor = sourceColor.rgb * 22.0;

    sumColor += texture2D(uSampler, blurCoordinates[0]).rgb;
    sumColor += texture2D(uSampler, blurCoordinates[1]).rgb;
    sumColor += texture2D(uSampler, blurCoordinates[2]).rgb;
    sumColor += texture2D(uSampler, blurCoordinates[3]).rgb;

    sumColor += texture2D(uSampler, blurCoordinates[4]).rgb;
    sumColor += texture2D(uSampler, blurCoordinates[5]).rgb;
    sumColor += texture2D(uSampler, blurCoordinates[6]).rgb;
    sumColor += texture2D(uSampler, blurCoordinates[7]).rgb;

    sumColor += texture2D(uSampler, blurCoordinates[8]).rgb * 2.0;
    sumColor += texture2D(uSampler, blurCoordinates[9]).rgb * 2.0;
    sumColor += texture2D(uSampler, blurCoordinates[10]).rgb * 2.0;
    sumColor += texture2D(uSampler, blurCoordinates[11]).rgb * 2.0;

    sumColor = sumColor / 38.0;

    float highPass = sourceColor.g - sumColor.g + 0.5;

    for (int i = 0; i < 5; i++) {
        highPass = hardLight(highPass);
    }

    float lumance = dot(sourceColor.rgb, W);
    float alpha = pow(lumance, uBeautyLevel);

    vec3 smoothColor = sourceColor.rgb + (sourceColor.rgb - vec3(highPass)) * alpha * 0.1;

    smoothColor.r = clamp(pow(smoothColor.r, 0.63), 0.0, 1.0);
    smoothColor.g = clamp(pow(smoothColor.g, 0.63), 0.0, 1.0);
    smoothColor.b = clamp(pow(smoothColor.b, 0.63), 0.0, 1.0);

    vec3 lvse = vec3(1.0) - (vec3(1.0) - smoothColor) * (vec3(1.0) - sourceColor.rgb);
    vec3 bianliang = max(smoothColor, sourceColor.rgb);
    vec3 rouguang = 2.0 * sourceColor.rgb * smoothColor + sourceColor.rgb * sourceColor.rgb - 2.0 * sourceColor.rgb * sourceColor.rgb * smoothColor;

    gl_FragColor = vec4(mix(sourceColor.rgb, lvse, alpha), 1.0);
    gl_FragColor.rgb = mix(gl_FragColor.rgb, bianliang, alpha);
    gl_FragColor.rgb = mix(gl_FragColor.rgb, rouguang, 0.4);

    vec3 satColor = gl_FragColor.rgb * saturateMatrix;
    gl_FragColor.rgb = mix(gl_FragColor.rgb, satColor, 0.35);
}