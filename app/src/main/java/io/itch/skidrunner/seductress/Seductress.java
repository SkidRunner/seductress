package io.itch.skidrunner.seductress;

import java.util.HashMap;
import com.jme3.app.SimpleApplication;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.material.Material;
import com.jme3.texture.Texture2D;
import com.jme3.texture.Texture;
import com.jme3.terrain.geomipmap.NormalRecalcControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.control.LodControl;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.terrain.heightmap.HeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.asset.AssetManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.CameraNode;

public class Seductress extends SimpleApplication {
	
	@Override
	public void simpleInitApp() {
		//flyCam.setEnabled(false);
		
		cam.setLocation(new Vector3f(0.0f, 2.0f, 0.0f));
		
		BulletAppState bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		PhysicsSpace physicsSpace = bulletAppState.getPhysicsSpace();
		
		Node scene = createScene(getAssetManager(), getCamera());
		rootNode.attachChild(scene);
		physicsSpace.addAll(scene);
		
	}
	
	private Node createScene(AssetManager assetManager, Camera camera) {
		Node scene = new Node("Scene");
		
		scene.attachChild(createSky(assetManager));
		scene.attachChild(createTerrain(assetManager, camera));
		//scene.attachChild(createCamera(camera));
		return scene;
	}
	
	private Spatial createSky(AssetManager assetManager) {
		Texture west = assetManager.loadTexture("Textures/Sky/West.png");
		Texture east = assetManager.loadTexture("Textures/Sky/East.png");
		Texture north = assetManager.loadTexture("Textures/Sky/North.png");
		Texture south = assetManager.loadTexture("Textures/Sky/South.png");
		Texture up = assetManager.loadTexture("Textures/Sky/Up.png");
		Texture down = assetManager.loadTexture("Textures/Sky/Down.png");
		Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
		sky.setName("Sky");
		return sky;
	}
	
	private Material createTerrainMaterial(AssetManager assetManager) {
		Texture alpha = assetManager.loadTexture("Textures/Terrain/Alpha.png");
		Texture tex1 = assetManager.loadTexture("Textures/Terrain/Tex1.png");
		tex1.setWrap(Texture.WrapMode.Repeat);
		Texture tex2 = assetManager.loadTexture("Textures/Terrain/Tex2.png");
		tex2.setWrap(Texture.WrapMode.Repeat);
		Texture tex3 = assetManager.loadTexture("Textures/Terrain/Tex3.png");
		tex3.setWrap(Texture.WrapMode.Repeat);

		Material material = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
		material.setTexture("Alpha", alpha);
		material.setTexture("Tex1", tex1);
		material.setFloat("Tex1Scale", 64.0f);
		material.setTexture("Tex2", tex2);
		material.setFloat("Tex2Scale", 64.0f);
		material.setTexture("Tex3", tex3);
		material.setFloat("Tex3Scale", 64.0f);
		
		return material;
	}
	
	private float[] loadHeightMap(AssetManager assetManager) {
		HeightMap heightMap = new ImageBasedHeightMap(assetManager.loadTexture("Textures/Terrain/HeightMap.png").getImage());
		heightMap.load();
		return heightMap.getHeightMap();
	}
	
	
	
	private float[] normalizeHeightMap(float[] heightMap) {
		return setHeightMapMinMax(heightMap, 0, 1);
	}
	
	private float[] setHeightMapMinMax(float[] heightMap, float min, float max) {
		float tMin = heightMap[0];
		float tMax = heightMap[0];
		
		for(int i = 1; i < heightMap.length; i++) {
			if(heightMap[i] < tMin) {
				tMin = heightMap[i];
			}
			if(heightMap[i] > tMax) {
				tMax = heightMap[i];
			}
		}
		
		for(int i = 1; i < heightMap.length; i++) {
			heightMap[i] = (((heightMap[i] - tMin) / (tMax - tMin)) * (max - min)) + min;
		}
		
		return heightMap;
	}
	
	private float[] createHeightMap(AssetManager assetManager) {
		float[] heightMap = loadHeightMap(assetManager);
		setHeightMapMinMax(heightMap, -50, 50);
		return heightMap;
	}
	
	private TerrainQuad createTerrain(AssetManager assetManager, Camera camera) {
		TerrainQuad terrain = new TerrainQuad("Terrain", 65, 513, createHeightMap(assetManager));
		terrain.setMaterial(createTerrainMaterial(assetManager));
		terrain.addControl(new TerrainLodControl(terrain, camera));
		terrain.addControl(new NormalRecalcControl(terrain));
		terrain.addControl(new RigidBodyControl(0));
		
		return terrain;
	}
	
	private CameraNode createCamera(Camera camera) {
		return new CameraNode("Camera", camera);
	}
	
}
