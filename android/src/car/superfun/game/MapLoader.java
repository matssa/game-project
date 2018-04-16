package car.superfun.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class MapLoader extends TmxMapLoader {

    World world;
    FixtureDef fixtureDef;

    public MapLoader(World world) {
        super();
        this.world = world;

        fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = GlobalVariables.WALL_ENTITY;
        fixtureDef.filter.maskBits = GlobalVariables.PLAYER_ENTITY | GlobalVariables.OPPONENT_ENTITY;
    }

    @Override
    protected void loadTileLayer (TiledMap map, MapLayers parentLayers, Element element) {
        if (element.getName().equals("layer")) {
            int width = element.getIntAttribute("width", 0);
            int height = element.getIntAttribute("height", 0);
            int tileWidth = map.getProperties().get("tilewidth", Integer.class);
            int tileHeight = map.getProperties().get("tileheight", Integer.class);
            TiledMapTileLayer layer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);

            loadBasicLayerInfo(layer, element);

            int[] ids = getTileIds(element, width, height);
            TiledMapTileSets tilesets = map.getTileSets();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int id = ids[y * width + x];
                    boolean flipHorizontally = ((id & FLAG_FLIP_HORIZONTALLY) != 0);
                    boolean flipVertically = ((id & FLAG_FLIP_VERTICALLY) != 0);
                    boolean flipDiagonally = ((id & FLAG_FLIP_DIAGONALLY) != 0);

                    TiledMapTile tile = tilesets.getTile(id & ~MASK_CLEAR);
                    if (tile != null) {
                        TiledMapTileLayer.Cell cell = createTileLayerCell(flipHorizontally, flipVertically, flipDiagonally);
                        cell.setTile(tile);
                        layer.setCell(x, flipY ? height - 1 - y : y, cell);
                        MapObjects mapObjects = tile.getObjects();
                        for (MapObject mapObject : mapObjects) {
                            Shape shape;

                            if (mapObject instanceof PolylineMapObject) {
                                shape = getPolyline((PolylineMapObject)mapObject, height, tileWidth, tileHeight, x, y);
                            } else {
                                Gdx.app.log("MapLoader", "encountered non-polyLine tileObject");
                                continue;
                            }

                            BodyDef bodyDef = new BodyDef();
                            bodyDef.type = BodyDef.BodyType.StaticBody;

                            fixtureDef.shape = shape;

                            Body body = world.createBody(bodyDef);
                            body.createFixture(fixtureDef);
                            shape.dispose();
                        }
                    }
                }
            }

            Element properties = element.getChildByName("properties");
            if (properties != null) {
                loadProperties(layer.getProperties(), properties);
            }
            parentLayers.add(layer);
        }
    }

    private static float getBox2dX(int tileXPos, int tileWidth) {
        return (tileXPos * tileWidth) / GlobalVariables.PIXELS_TO_METERS;
    }

    private static float getBox2dY(int tileYPos, int tileHeight, int height) {
        return ((height - tileYPos - 1) * tileHeight) / GlobalVariables.PIXELS_TO_METERS;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineMapObject, int height, int tileWidth, int tileHeight, int x, int y) {
        float[] vertices = polylineMapObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = (vertices[i * 2] / GlobalVariables.PIXELS_TO_METERS) + getBox2dX(x, tileWidth);
            worldVertices[i].y = (vertices[i * 2 + 1] / GlobalVariables.PIXELS_TO_METERS) + getBox2dY(y, tileHeight, height);
        }

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(worldVertices);
        return chainShape;
    }
}
