package car.superfun.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class TrackBuilder {

    public static Array<Vector2> getPoints(Map map, String layerName) {
        MapObjects mapObjects = map.getLayers().get(layerName).getObjects();
        Array<Vector2> points = new Array<>();

            for (MapObject mapObject : mapObjects) {
                float x = Float.parseFloat(mapObject.getProperties().get("x").toString());
                float y = Float.parseFloat(mapObject.getProperties().get("y").toString());
                points.add(new Vector2(x, y));
            }

        return points;
    }

    public static Array<Body> buildTileLayer(TiledMap map, World world, String layerName, FixtureDef fixtureDef, String tileSetName) {
        Gdx.app.log("TileLayer", "in buildTileLayer");
//        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerName);
//        TiledMapTile tile;

        TiledMapTileSet tileSet = map.getTileSets().getTileSet(tileSetName);

        Array<Body> bodies = new Array<>();

        for (TiledMapTile tile : tileSet) {
            MapObjects mapObjects = tile.getObjects();
            for (MapObject mapObject : mapObjects) {
                Shape shape;
                Gdx.app.log("TileLayer", "mapObject name: " + mapObject.getName());
                Gdx.app.log("TileLayer", "mapObject to string: " + mapObject.toString());

                try {
                    shape = getShape(mapObject);
                } catch (ClassNotFoundException ex) {
                    Gdx.app.error("Can't find class for mapObject", mapObject.toString());
                    return bodies;
                }

                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;

                fixtureDef.shape = shape;

                Body body = world.createBody(bodyDef);
                body.createFixture(fixtureDef);
                bodies.add(body);
                shape.dispose();
            }
        }



        return bodies;
    }

    public static Array<Body> buildLayer(Map map, World world, String layerName, FixtureDef fixtureDef) {
        MapObjects mapObjects = map.getLayers().get(layerName).getObjects();
        Array<Body> bodies = new Array<>();

        for (MapObject mapObject : mapObjects) {
            Shape shape;
            try {
                shape = getShape(mapObject);
            } catch (ClassNotFoundException ex) {
                Gdx.app.error("Can't find class for mapObject", mapObject.toString());
                return bodies;
            }

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            fixtureDef.shape = shape;

            Body body = world.createBody(bodyDef);
            body.createFixture(fixtureDef);
            bodies.add(body);
            shape.dispose();
        }

        return bodies;
    }

    public static Array<Body> buildLayerWithUserData(Map map, World world, String layerName, FixtureDef fixtureDef, UserDataCreater userDataCreater) {
        MapObjects mapObjects = map.getLayers().get(layerName).getObjects();
        Array<Body> bodies = new Array<>();

        for (MapObject mapObject : mapObjects) {
            Shape shape;
            try {
                shape = getShape(mapObject);
            } catch (ClassNotFoundException ex) {
                Gdx.app.error("Can't find class for mapObject", mapObject.toString());
                return bodies;
            }

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            fixtureDef.shape = shape;

            Body body = world.createBody(bodyDef);
            body.setUserData(userDataCreater.getUserData());
            body.createFixture(fixtureDef);
            bodies.add(body);
            shape.dispose();
        }

        return bodies;
    }

    private static Shape getShape(MapObject mapObject) throws ClassNotFoundException {
        Shape shape;
        if (mapObject instanceof TextureMapObject) {
            Gdx.app.log("mapObject", "is instance of textureMapObject");
            shape = getTile((TiledMapTileMapObject) mapObject);
//            shape = getRectangle((RectangleMapObject) mapObject);
        }
        else if (mapObject instanceof PolygonMapObject) {
            shape = getPolygon((PolygonMapObject)mapObject);
        }
        else if (mapObject instanceof PolylineMapObject) {
            shape = getPolyline((PolylineMapObject)mapObject);
        }
        else if (mapObject instanceof CircleMapObject) {
            shape = getCircle((CircleMapObject)mapObject);
        } else {
            throw new ClassNotFoundException("Cannot find class for mapObject");
        }
        return shape;
    }

    private static PolygonShape getTile(TiledMapTileMapObject tileObject) {
//        Sprite sprite;
//        tileObject.getTile().getTextureRegion().getTexture();

//        float x = tileObject.
        Gdx.app.log("in method", "PolygonShape");

        Gdx.app.log("getX()", "" + tileObject.getX());
        Gdx.app.log("getScaleX()", "" + tileObject.getScaleX());
        Gdx.app.log("getRegionWidth()", "" + tileObject.getTile().getTextureRegion().getRegionWidth());

//        shape.setAsBox(tileObject.getTile().getTextureRegion().getRegionWidth() );
//        shape.setAsBox((sprite.getWidth() / 2) / CarSuperFun.PIXELS_TO_METERS, (sprite.getHeight() / 2) / CarSuperFun.PIXELS_TO_METERS);

        float width = tileObject.getTextureRegion().getRegionWidth() / (2 * GlobalVariables.PIXELS_TO_METERS);
        float height = tileObject.getTextureRegion().getRegionHeight() / (2 * GlobalVariables.PIXELS_TO_METERS);

        Vector2 center = new Vector2((tileObject.getX() / GlobalVariables.PIXELS_TO_METERS) + width, (tileObject.getY() / GlobalVariables.PIXELS_TO_METERS) + height);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height, center, 0f);
        return shape;
    }

    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle;
        PolygonShape polygonShape;
        Vector2 size;

        rectangle = rectangleObject.getRectangle();
        polygonShape = new PolygonShape();
        size = new Vector2((rectangle.x + rectangle.width * 0.5f) / GlobalVariables.PIXELS_TO_METERS,(rectangle.y + rectangle.height * 0.5f ) / GlobalVariables.PIXELS_TO_METERS);
        polygonShape.setAsBox(rectangle.width * 0.5f / GlobalVariables.PIXELS_TO_METERS, rectangle.height * 0.5f / GlobalVariables.PIXELS_TO_METERS, size, 0.0f);
        return polygonShape;
    }

    private static CircleShape getCircle(CircleMapObject circleMapObject) {
        Circle circle;
        CircleShape circleShape;

        circle = circleMapObject.getCircle();
        circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / GlobalVariables.PIXELS_TO_METERS);
        circleShape.setPosition(new Vector2(circle.x / GlobalVariables.PIXELS_TO_METERS, circle.y / GlobalVariables.PIXELS_TO_METERS));
        return circleShape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonMapObject) {
        PolygonShape polygonShape;
        float[] vertices;
        float[] worldVertices;

        polygonShape = new PolygonShape();
        vertices = polygonMapObject.getPolygon().getTransformedVertices();
        worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            worldVertices[i] = vertices[i] / GlobalVariables.PIXELS_TO_METERS;
        }

        polygonShape.set(worldVertices);
        return polygonShape;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineMapObject) {
        float[] vertices = polylineMapObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / GlobalVariables.PIXELS_TO_METERS;
            worldVertices[i].y = vertices[i * 2 + 1] / GlobalVariables.PIXELS_TO_METERS;
        }

        ChainShape chainShape = new ChainShape();
        chainShape.createChain(worldVertices);
        return chainShape;
    }

}