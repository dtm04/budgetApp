package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.HashMap;

public class ScreensController extends StackPane {

    private HashMap<String, Node> screens = new HashMap<>();

    public ScreensController(){
        super();
    }

    //
    public void addScreen(String name, Node screen) {
        screens.put(name,screen);
    }

    //returns the node with name
    public Node getScreen(String name){
        return screens.get(name);
    }

    //loads FXML file add screen to collection injects screen pane to controller
    public boolean loadScreen(String name, String resource) {
        try{
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = myLoader.load();
            ControlledScreen myScreenController = myLoader.getController();
            myScreenController.setScreenParent(this);
            addScreen(name, loadScreen);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //checks if screen is loaded, then checks that getchildren is not empty, then puts the screen at index 0
    public boolean setScreen(final String name){
        if(screens.get(name) != null) {
            if (!getChildren().isEmpty()) {
                getChildren().remove(0);
                getChildren().add(0, screens.get(name));

            } else {
                getChildren().add(screens.get(name));
            }
            return true;
        }else {
            System.out.println("screen wasn't loaded \n");
            return false;
        }
    }



    public boolean unloadScreen(String name){
        if(screens.remove(name) == null) {
            System.out.println("no screen to remove");
            return false;
        } else {
            return true;
        }

    }
}
