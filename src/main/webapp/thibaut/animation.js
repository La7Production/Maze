ParticleSystem ps;

void setup() {

	size(350,400);
	X = width / 2;
	Y = height-350;
	frameRate(60);
	colorMode(RGB,255,200,255,100);

	ps = new ParticleSystem(1,new Vector3D(X,Y,0));
}

void drawText(String t){
	background(#ffffff);
	float twidth = textWidth(t);
	text(t, (width - twidth)/2, height-50); 
	textSize(20);
}

void draw() {	
	background(0);

	drawText("En attente des joueurs ...");
	ps.run();

	ps.addParticle();
}

class Particle {

	Vector3D loc;

	Vector3D vel;

	Vector3D acc;

	float r;

	float timer;

	Particle(Vector3D a, Vector3D v, Vector3D l, float r_) {

		acc = a.copy();

		vel = v.copy();

		loc = l.copy();

		r = r_;
	}


	Particle(Vector3D l) {

		acc = new Vector3D(0,0.05,0);

		vel = new Vector3D(random(-1,1),random(-2,0),0);

		loc = l.copy();

		fill(0);
		r = 15.0;

		timer = 150.0;

	}


	void run() {

		update();
		render();

	}



	// Method to update location

	void update() {

		vel.add(acc);

		loc.add(vel);

		timer -= 1.0;

	}



	// Method to display

	void render() {

		ellipseMode(CENTER);

		noStroke();
		color c1 = color(0, 0, 0);	
		fill(c1,timer);

		ellipse(loc.x,loc.y,r,r);

	}



	// Is the particle still useful?

	boolean dead() {

		if (timer <= 0.0) {

			return true;

		} else {

			return false;

		}

	}

}

void mouseMoved(){
	nX = mouseX;
	nY = mouseY;  
}



// A class to describe a group of Particles

// An ArrayList is used to manage the list of Particles 



class ParticleSystem {



	ArrayList particles; 

	Vector3D origin;        



	ParticleSystem(int num, Vector3D v) {

		particles = new ArrayList();              

		origin = v.copy();                        

		for (int i = 0; i < num; i++) {

			particles.add(new Particle(origin));    

		}

	}



	void run() {

		for (int i = particles.size()-1; i >= 0; i--) {

			Particle p = (Particle) particles.get(i);

			p.run();

			if (p.dead()) {

				particles.remove(i);

			}

		}

	}



	void addParticle() {

		particles.add(new Particle(origin));

	}



	void addParticle(Particle p) {

		particles.add(p);

	}


	boolean dead() {

		if (particles.isEmpty()) {

			return true;

		} else {

			return false;

		}

	}



}


public class Vector3D {

	public float x;

	public float y;

	public float z;



	Vector3D(float x_, float y_, float z_) {

		x = x_; y = y_; z = z_;

	}



	Vector3D(float x_, float y_) {

		x = x_; y = y_; z = 0f;

	}



	Vector3D() {

		x = 0f; y = 0f; z = 0f;

	}



	void setX(float x_) {

		x = x_;

	}



	void setY(float y_) {

		y = y_;

	}



	void setZ(float z_) {

		z = z_;

	}



	void setXY(float x_, float y_) {

		x = x_;

		y = y_;

	}



	void setXYZ(float x_, float y_, float z_) {

		x = x_;

		y = y_;

		z = z_;

	}



	void setXYZ(Vector3D v) {

		x = v.x;

		y = v.y;

		z = v.z;

	}

	public float magnitude() {

		return (float) Math.sqrt(x*x + y*y + z*z);

	}



	public Vector3D copy() {

		return new Vector3D(x,y,z);

	}



	public Vector3D copy(Vector3D v) {

		return new Vector3D(v.x, v.y,v.z);

	}



	public void add(Vector3D v) {

		x += v.x;

		y += v.y;

		z += v.z;

	}



	public void sub(Vector3D v) {

		x -= v.x;

		y -= v.y;

		z -= v.z;

	}



	public void mult(float n) {

		x *= n;

		y *= n;

		z *= n;

	}



	public void div(float n) {

		x /= n;

		y /= n;

		z /= n;

	}



	public void normalize() {

		float m = magnitude();

		if (m > 0) {

			div(m);

		}

	}



	public void limit(float max) {

		if (magnitude() > max) {

			normalize();

			mult(max);

		}

	}



	public float heading2D() {

		float angle = (float) Math.atan2(-y, x);

		return -1*angle;

	}



	public Vector3D add(Vector3D v1, Vector3D v2) {

		Vector3D v = new Vector3D(v1.x + v2.x,v1.y + v2.y, v1.z + v2.z);

		return v;

	}



	public Vector3D sub(Vector3D v1, Vector3D v2) {

		Vector3D v = new Vector3D(v1.x - v2.x,v1.y - v2.y,v1.z - v2.z);

		return v;

	}



	public Vector3D div(Vector3D v1, float n) {

		Vector3D v = new Vector3D(v1.x/n,v1.y/n,v1.z/n);

		return v;

	}



	public Vector3D mult(Vector3D v1, float n) {

		Vector3D v = new Vector3D(v1.x*n,v1.y*n,v1.z*n);

		return v;

	}



	public float distance (Vector3D v1, Vector3D v2) {

		float dx = v1.x - v2.x;

		float dy = v1.y - v2.y;

		float dz = v1.z - v2.z;

		return (float) Math.sqrt(dx*dx + dy*dy + dz*dz);

	}


}