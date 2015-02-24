package assignment05.calendar;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class CalendarMainController {

	
	
    ObservableList<Appointment> data = FXCollections.observableArrayList(new Appointment("Kaffikaffikaffi", "Rom XXYY", LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(2)), new Appointment("Møte med X", "Rom XXYY", LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(2)), new Appointment("Møte med X", "Rom XXYY", LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours(2)));

    @FXML
    private ListView<Appointment> agenda_list_view;
    

    @FXML
    private HBox header_new_appointment_hbox;

    @FXML
	private void initialize() {
    	
    	assert agenda_list_view != null : "fx:id=\"agendaListView\" was not injected: check your FXML file 'ListItem_Agenda.fxml'.";
    	
    	
    	agenda_list_view.setItems(data);
    	 
    	agenda_list_view.setCellFactory(new Callback<ListView<Appointment>, 
                 ListCell<Appointment>>() {
     				@Override
     				public ListCell<Appointment> call(ListView<Appointment> list) {
     					return new AppointmentRectCell();
     				}
                 }
             );
    	/*
    	header_new_appointment_hbox.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
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
    	*/
    	
    }
    
    
    static class AppointmentRectCell extends ListCell<Appointment> {
        @Override
        public void updateItem(Appointment item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
            } else {
                ListItem_Agenda agendaItem =  new ListItem_Agenda();
                agendaItem.setInfo(item);
                setGraphic(agendaItem.getAnchorPane());
            }

        }
    }
    

}
