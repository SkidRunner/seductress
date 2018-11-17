package me.skidrunner.mygame;
import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;

public class MyGame extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		flyCam.setEnabled(false);
		float width = getCamera().getWidth();
		float height = getCamera().getHeight();
		
		Node scaledGuiNode = new Node("Scaled GUI Node");
		
	}
	
}
