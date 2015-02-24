package application;
	
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import controller.Controller;

public class Main extends Application {
	
	private static Appointment appointment;
	
	private final Stage editStage = new Stage();
	
	@Override
	public void start(Stage primaryStage) {
		Appointment appointment = new Appointment();
		setAppointment(appointment);
		
		BorderPane root = new BorderPane();
        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(400);
        primaryStage.setHeight(200);
        
        Button button = new Button("Rediger avtale");
        button.setStyle("-fx-font-size: 24;");
        
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) { 	
    			try {
        			FXMLLoader fxmlLoader = new FXMLLoader();
        			Parent root = (Parent)fxmlLoader.load(this.getClass().getResourceAsStream("View.fxml"));
        			
        			editStage.setScene(new Scene(root));
        			
        			Controller controller = fxmlLoader.<Controller>getController();
					controller.initialize(getAppointment());
        			
        			editStage.show();
        		} catch(Exception e) {
        			e.printStackTrace();
        		}
            }
        });
        root.setCenter(button);
        primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static Appointment getAppointment() {
        return appointment;
    }

    public static void setAppointment(Appointment appointment) {
        Main.appointment = appointment;
    }
}
