#version 410

uniform sampler2D u_texture;
uniform ivec2 u_screenSize;
uniform float u_thickness;

in vec2 v_texcoord;	// UV 

layout(location = 0) out vec4 o_colour;

// Edge detection filter

const float conv[9] = float[9](
    -1.,-1.,-1.,
    -1.,8.,-1.,
    -1.,-1.,-1.
);

void main() {
	vec2 pixelSize = u_thickness / u_screenSize;

	// Use a convolution skernel to find the places where depth changes quickly

    float edge = 0;
	int k = 0;
    for (int i = -1; i <= 1; i++){
	    for (int j = -1; j <= 1; j++){
	        vec2 d = vec2(i, j);
    		edge += conv[k] * texture(u_texture, v_texcoord + d * pixelSize).w;
    		k++;
    	}
    }
	edge = abs(edge/9.);
	
	// mix between black and the target colour depending on the edge value
	
	vec3 colour = texture(u_texture, v_texcoord).rgb;
	
    o_colour = vec4(mix(colour, vec3(0), smoothstep(0, 0.1, edge)), 1);    
}

