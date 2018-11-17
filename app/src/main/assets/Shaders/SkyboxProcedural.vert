#import "Common/ShaderLib/GLSLCompat.glsllib"
uniform mat4 g_ViewMatrix;
uniform mat4 g_ProjectionMatrix;
uniform mat4 g_WorldMatrixInverse;

uniform vec4 m_SunColor;
uniform float m_SunStrength;
uniform vec4 m_AtmosphereColor;
uniform vec4 m_GroundColor;
uniform float m_Exposure;

attribute vec3 inPosition;
attribute vec3 inNormal;

varying vec4 position;
varying vec3 direction;
varying vec3 inScatter;
varying vec3 outScatter;

// RGB wavelengths
#define GAMMA .454545
const float MN = 2;
const float MX = .7;
#define WR (0.65*lerp(MN, MX, pow(_Color.r,GAMMA)))
#define WG (0.57*lerp(MN, MX, pow(_Color.g,GAMMA)))
#define WB (0.475*lerp(MN, MX, pow(_Color.b,GAMMA)))
//#define WR pow(0.65,GAMMA)
//#define WG pow(0.57,GAMMA)
//#define WB pow(0.475,GAMMA)
const vec3 kInvWavelength = vec3(1.0 / (WR*WR*WR*WR), 1.0 / (WG*WG*WG*WG), 1.0 / (WB*WB*WB*WB));
#define OUTER_RADIUS 1.025
const float kOuterRadius = OUTER_RADIUS;
const float kOuterRadius2 = OUTER_RADIUS*OUTER_RADIUS;
const float kInnerRadius = 1.0;
const float kInnerRadius2 = 1.0;

const float kCameraHeight = 0.0001;

// Rayleigh constant
#define kRAYLEIGH 0.0025
// Mie constant
#define kMIE 0.0010
// Sun brightness
#define kSUN_BRIGHTNESS 20.0

const float kKrESun = kRAYLEIGH * kSUN_BRIGHTNESS;
const float kKmESun = kMIE * kSUN_BRIGHTNESS;
const float kKr4PI = kRAYLEIGH * 4.0 * 3.14159265;
const float kKm4PI = kMIE * 4.0 * 3.14159265;
const float kScale = 1.0 / (OUTER_RADIUS - 1.0);
const float kScaleDepth = 0.25;
const float kScaleOverScaleDepth = (1.0 / (OUTER_RADIUS - 1.0)) / 0.25;
const float kSamples = 2.0; // THIS IS UNROLLED MANUALLY, DON'T TOUCH

#define MIE_G (-0.990)
#define MIE_G2 0.9801

void main(){
    // set w coordinate to 0
    position = vec4(inPosition, 0.0);

    // compute rotation only for view matrix
    position = g_ViewMatrix * position;

    // now find projection
    position.w = 1.0;
    position = g_ProjectionMatrix * position;
    gl_Position = position;
    
    vec4 normal = vec4(inNormal, 0.0);
    direction = (g_WorldMatrixInverse * normal).xyz;
    
    float far = 0.0;
    if(direction.y >= 0.0)
    {
        
    }
    else
    {
        far = (-kCameraHeight) / (min(-0.00001, eyeRay.y));
        vec3 pos = cameraPos + far * eyeRay;
        
        // Calculate the ray's starting position, then calculate its scattering offset
        float depth = exp((-kCameraHeight) * (1.0/kScaleDepth));
        float cameraAngle = dot(-eyeRay, pos);
        float lightAngle = dot(_WorldSpaceLightPos0.xyz, pos);
        float cameraScale = scale(cameraAngle);
        float lightScale = scale(lightAngle);
        float cameraOffset = depth*cameraScale;
        float temp = (lightScale + cameraScale);
        
        // Initialize the scattering loop variables
        float sampleLength = far / kSamples;
        float scaledLength = sampleLength * kScale;
        vec3 sampleRay = eyeRay * sampleLength;
        vec3 samplePoint = cameraPos + sampleRay * 0.5;
        
        // Now loop through the sample rays
        vec3 frontColor = vec3(0.0, 0.0, 0.0);
        vec3 attenuate;
        for(int i=0; i<int(kSamples); i++)
        {
            float height = length(samplePoint);
            float depth = exp(kScaleOverScaleDepth * (kInnerRadius - height));
            float scatter = depth*temp - cameraOffset;
            attenuate = exp(-scatter * (kInvWavelength * kKr4PI + kKm4PI));
            frontColor += attenuate * (depth * scaledLength);
            samplePoint += sampleRay;
        }
        
        inScatterCoefficient.xyz = frontColor * (kInvWavelength * kKrESun + kKmESun);
        outScatterCoefficient.xyz = clamp(attenuate, 0.0, 1.0);
    }
}
