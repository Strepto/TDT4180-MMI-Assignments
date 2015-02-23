package assignment05.calendar;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ListItem_Agenda {


    @FXML
    private AnchorPane item_activity_root_anchorpane;
	
    @FXML
    private Label item_activity_dateplace_label;

    @FXML
    private Label item_activity_formal_label;
    

    
    public ListItem_Agenda() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ListItem_Agenda.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        assert item_activity_root_anchorpane != null : "Error with item_activity_root_anchorpane in " + this.getClass().getName();
    }

    public void setInfo(Appointment appointment){
    	item_activity_formal_label.setText(appointment.getFormal());
    	
    	String template = "%s - %s ved %s";

    	String message = String.format(template, appointment.getFra().format(DateTimeFormatter.ISO_TIME).toString(), appointment.getTil().format(DateTimeFormatter.ISO_TIME).toString(), appointment.getRom());    	
    	item_activity_dateplace_label.setText(message);
    }
    
    
    public AnchorPane getAnchorPane(){
    	return item_activity_root_anchorpane;
    }
}
