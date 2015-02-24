package controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import application.Appointment;
import application.Main;

public class Controller {
	private ArrayList<String> activeErrorMessages = new ArrayList<>();
	
	// ERROR MESSAGES - empty field
	private final String errorPurposeFieldEmpty = "Du må oppgi et formål for møtet.";
	private final String errorRoomFieldEmpty = "Du må oppgi hvor møtet skal finne sted.";
	private final String errorStartDatePickerEmpty = "Du må oppgi en dato.";
	private final String errorStartHourFieldEmpty = "Du må oppgi et timetall for starttidspunktet.";
	private final String errorStartMinuteFieldEmpty = "Du må oppgi et minuttall for starttidspunktet.";
	private final String errorEndHourFieldEmpty = "Du må oppgi et timetall for sluttidspunktet.";
	private final String errorEndMinuteFieldEmpty = "Du må oppgi et minuttall for sluttidspunktet.";
	private final String errorRepeatFreqFieldEmpty = "Du må oppgi en repetisjonsfrekvens (evt. deaktivere repetisjon.";
	private final String errorEndDatePickerEmpty = "Du må oppgi en sluttdato (evt. deaktivere repetisjon.";
	
	// ERROR MESSAGES - illegal character
	private final String errorIllegalCharInRoomField = "Feltet for sted skal inneholde navn på bygning (bokstaver, mellomrom, bindestrek og tall), et mellomrom, og til slutt romnummer (kun tall).";
	private final String errorIllegalCharInStartHourField = "Feltet for start-time kan kun inneholde et heltall mellom 0 og 23.";
	private final String errorIllegalCharInStartMinuteField = "Feltet for start-minutt kan kun inneholde et to-siffret tall mellom 00 og 59.";
	private final String errorIllegalCharInEndHourField = "Feltet for slutt-time kan kun inneholde et heltall mellom 0 og 23.";
	private final String errorIllegalCharInEndMinuteField = "Feltet for slutt-minutt kan kun inneholde et to-siffret tall mellom 00 og 59.";
	private final String errorIllegalCharInRepeatFreqField = "Feltet for repetisjonsfrekvens kan kun inneholde et tall større enn 0.";
	
	// ERROR MESSAGES - other
	private final String errorEndTimeBeforeStartTime = "Slutt-tidspunktet er kan ikke være før start-tidspunktet.";
	private final String errorEndDateBeforeStartDate = "Slutt-datoen må være etter start-datoen.";
	private final String errorEndDateFrequencyMismatch = "Slutt-datoen samsvarer ikke med repetisjonsfrekvensen.";
	
	private Appointment appointment;
	
	@FXML
    private TextField purposeField, roomField, startHourField, startMinuteField, endHourField, endMinuteField, repeatFreqField;

	@FXML
	private DatePicker startDatePicker, endDatePicker;
	
	@FXML
	private CheckBox repeatCheckbox;
	
	@FXML
	private ScrollPane errorPane;
	
	@FXML
	private Label errorLabel;
	
	@FXML
	private Button cancelBtn, saveBtn;

	@FXML
	public void initialize(Appointment appointment) {
		this.appointment = appointment;
		
		// Set data to form if exists in appointment object
		if (appointment.getFormal() != null) purposeField.setText(appointment.getFormal());
		if (appointment.getRom() != null) roomField.setText(appointment.getRom());
		if (appointment.getDato() != null) startDatePicker.setValue(appointment.getDato());
		if (appointment.getFra() != null) {
			String firstCharStartMinute = (appointment.getFra().getMinute() < 10) ? "0" : "";
			startHourField.setText(Integer.toString(appointment.getFra().getHour()));
			startMinuteField.setText(firstCharStartMinute + Integer.toString(appointment.getFra().getMinute()));
		}
		if (appointment.getTil() != null) {
			String firstCharEndMinute = (appointment.getTil().getMinute() < 10) ? "0" : "";
			endHourField.setText(Integer.toString(appointment.getTil().getHour()));
			endMinuteField.setText(firstCharEndMinute + Integer.toString(appointment.getTil().getMinute()));
		}
		if (appointment.getRepetisjon() != null) {
			if (appointment.getRepetisjon() > 0) {
				repeatCheckbox.setSelected(true);
				enableRepeatOptions(true);
				repeatFreqField.setText(Integer.toString(appointment.getRepetisjon()));
			}
		}
		if (appointment.getSlutt() != null) endDatePicker.setValue(appointment.getSlutt());
		
		// Add listener to purpose field
		purposeField.focusedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
		    	if (!newPropertyValue.equals("")) activeErrorMessages.remove(errorPurposeFieldEmpty);
		    }
		});
		
		// Add listener to room field
		roomField.focusedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
		        validateCharacters(newPropertyValue, roomField, "^[a-zæøåA-Zæøå0-9 -]* [0-9]*$", errorIllegalCharInRoomField);
		        if (!newPropertyValue.equals("")) activeErrorMessages.remove(errorRoomFieldEmpty);
		    }
		});
		
		// Add listener to start date picker
		startDatePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
		    @Override
		    public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
		        if (repeatCheckbox.isSelected()) {
		        	if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
		        		checkEndDateAfterStartDate();
			    	}
		    	}
		        if (startDatePicker.getValue() != null) activeErrorMessages.remove(errorStartDatePickerEmpty);
		    }
		});
		
		// Add listener to start hour field
		startHourField.focusedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
		    	boolean validChars = validateCharacters(newPropertyValue, startHourField, "([01]?[0-9]|2[0-3])", errorIllegalCharInStartHourField);
		    	if (validChars && !newPropertyValue && !startHourField.getText().equals("")) {
		    		checkEndTimeAfterStartTime();
		    	}
		    	if (!newPropertyValue.equals("")) activeErrorMessages.remove(errorStartHourFieldEmpty);
		    }
		});
		
		// Add listener to start minute field
		startMinuteField.focusedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
		        boolean validChars = validateCharacters(newPropertyValue, startMinuteField, "[0-5][0-9]", errorIllegalCharInStartMinuteField);
		        if (validChars && !newPropertyValue && !startMinuteField.getText().equals("")) {
		    		checkEndTimeAfterStartTime();
		    	}
		        if (!newPropertyValue.equals("")) activeErrorMessages.remove(errorStartMinuteFieldEmpty);
		    }
		});
		
		// Add listener to end hour field
		endHourField.focusedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
		    	boolean validChars = validateCharacters(newPropertyValue, endHourField, "([01]?[0-9]|2[0-3])", errorIllegalCharInEndHourField);
		    	if (validChars && !newPropertyValue && !endHourField.getText().equals("")) {
		    		checkEndTimeAfterStartTime();
		    	}
		    	if (!newPropertyValue.equals("")) activeErrorMessages.remove(errorEndHourFieldEmpty);
		    }
		});
		
		// Add listener to end minute field
		endMinuteField.focusedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
		        
		    	boolean validChars = validateCharacters(newPropertyValue, endMinuteField, "[0-5][0-9]", errorIllegalCharInEndMinuteField);
		    	if (validChars && !newPropertyValue && !endMinuteField.getText().equals("")) {
		    		checkEndTimeAfterStartTime();
		    	}
		    	if (!newPropertyValue.equals("")) activeErrorMessages.remove(errorEndMinuteFieldEmpty);
		    }
		});
		
		// Add listener to repeat frequency field
		repeatFreqField.focusedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
		        boolean validChars = validateCharacters(newPropertyValue, repeatFreqField, "^[1-9][0-9]*$", errorIllegalCharInRepeatFreqField);
		        if (validChars && !newPropertyValue && startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
		        	checkEndDateMatchesRepeatFreq();
		        }
		        if (!newPropertyValue.equals("")) activeErrorMessages.remove(errorRepeatFreqFieldEmpty);
		    }
		});
		
		// Add listener to end date picker
		endDatePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
		    @Override
		    public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
		    	if (endDatePicker.getValue() != null && startDatePicker.getValue() != null) {
	    			checkEndDateAfterStartDate();
		    		if (!repeatFreqField.getText().equals("") && !activeErrorMessages.contains(errorIllegalCharInRepeatFreqField)) {
		    			checkEndDateMatchesRepeatFreq();
		    		}
		    	}
		    	if (endDatePicker.getValue() != null) activeErrorMessages.remove(errorEndDatePickerEmpty);
		    }
		});
	}
	
	// Called when save button is clicked
	@FXML
    public void handleSaveBtnAction(ActionEvent event) {
		checkEmptyFields();
		if (activeErrorMessages.size() == 0) {
			appointment.setFormal(purposeField.getText());
			appointment.setRom(roomField.getText());
			appointment.setDato(startDatePicker.getValue());
			appointment.setFra(LocalTime.of(Integer.parseInt(startHourField.getText()), Integer.parseInt(startMinuteField.getText())));
			appointment.setTil(LocalTime.of(Integer.parseInt(endHourField.getText()), Integer.parseInt(endMinuteField.getText())));
			if (repeatCheckbox.isSelected()) {
				appointment.setRepetisjon(Integer.parseInt(repeatFreqField.getText()));
				appointment.setSlutt(endDatePicker.getValue());
			} else {
				appointment.setRepetisjon(0);
			}
			
			Main.setAppointment(appointment);
			
			Node  source = (Node)  event.getSource(); 
		    Stage stage  = (Stage) source.getScene().getWindow();
		    stage.close();
		    /*
		    //Alert alert = new Alert(AlertType.INFORMATION);
		    alert.setTitle("Endringer lagret");
		    alert.setHeaderText("Endringer lagret");
		    alert.setContentText("Avtalen har blitt lagret.");
		    
		    alert.showAndWait();
		    */
		}
    }

	// Called when cancel button is clicked
	@FXML
    public void handleCancelBtnAction(ActionEvent event) {
		Node  source = (Node)  event.getSource(); 
	    Stage stage  = (Stage) source.getScene().getWindow();
	    stage.close();
    }
	
	// Called when repeat checkbox changes value
	@FXML
    public void handleRepeatCheckboxAction(ActionEvent event) {
		if (event.getSource() instanceof CheckBox) {
            CheckBox chk = (CheckBox) event.getSource();
        	enableRepeatOptions(chk.isSelected());        
        }
    }
	
	// Enables or disables repeat options
	private void enableRepeatOptions(boolean enable) {
		repeatFreqField.setDisable(!enable);
    	endDatePicker.setDisable(!enable);
	}
	
	// Compares a string to a regEx, and adds/removes error messages
	private boolean validateCharacters(Boolean newPropertyValue, TextField textField, String regEx, String errorMsg) {
		boolean ok = true;
		if (!newPropertyValue && !textField.getText().equals("")) { // When textfield loses focus and is not empty
        	if (textField.getText().matches(regEx)) {
        		textField.setStyle("-fx-text-inner-color: black;");
    			activeErrorMessages.remove(errorMsg);
    		} else {
    			if (!activeErrorMessages.contains(errorMsg)) {
    				activeErrorMessages.add(errorMsg);
    				ok = false;
    			}
    			textField.setStyle("-fx-text-inner-color: red;");
    		};
    		updateErrorList();
        }
		return ok;
	}
	
	// Checks if the end time is set after the start time
	private void checkEndTimeAfterStartTime() {
		boolean ok = true;
		if (!startHourField.getText().equals("") && !endHourField.getText().equals("")) {
			try {
				int startHour = Integer.parseInt(startHourField.getText());
				int endHour = Integer.parseInt(endHourField.getText());
				if (startHour > endHour) {
					ok = false;
				} else if (startHour == endHour && !startMinuteField.getText().equals("") && !endMinuteField.getText().equals("")) {
					int startMinute = Integer.parseInt(startMinuteField.getText());
					int endMinute = Integer.parseInt(endMinuteField.getText());
					ok = (startMinute < endMinute);
				}
				
		    } catch (NumberFormatException e) {
		        // A time text field contains invalid chars.
		    	// User must input valid chars before input can be compared.
		    	// EndTimeBeforeStartTime is not shown yet.
		    	activeErrorMessages.remove(errorEndTimeBeforeStartTime);
				updateErrorList();
		    }
		}
		
		String textColor = "black";
		if (ok) {
			activeErrorMessages.remove(errorEndTimeBeforeStartTime);
			
		} else {
			textColor = "red";
			if (!activeErrorMessages.contains(errorEndTimeBeforeStartTime)) {
				activeErrorMessages.add(errorEndTimeBeforeStartTime);
			}
		}
		
		startHourField.setStyle("-fx-text-inner-color: " + textColor + ";");
		endHourField.setStyle("-fx-text-inner-color: " + textColor + ";");
		startMinuteField.setStyle("-fx-text-inner-color: " + textColor + ";");
		endMinuteField.setStyle("-fx-text-inner-color: " + textColor + ";");
		
		updateErrorList();
	}
	
	// Checks if the end date is set after the start date
	private void checkEndDateAfterStartDate() {
		long diff = ChronoUnit.DAYS.between(startDatePicker.getValue(), endDatePicker.getValue());
		String textColor = "black";
		if (diff > 0) {
			activeErrorMessages.remove(errorEndDateBeforeStartDate);
		} else {
			textColor = "red";
			if (!activeErrorMessages.contains(errorEndDateBeforeStartDate)) {
				activeErrorMessages.add(errorEndDateBeforeStartDate);
			}
		}
		startDatePicker.setStyle("-fx-text-inner-color: " + textColor + ";");
		endDatePicker.setStyle("-fx-text-inner-color: " + textColor + ";");
		
		updateErrorList();
	}
	
	// Checks that the end date matches the repeat frequency
	// (e.g. if frequency is weekly and start date is a Monday, end date cannot be a Tuesday)
	private void checkEndDateMatchesRepeatFreq() {
		long diff = ChronoUnit.DAYS.between(startDatePicker.getValue(), endDatePicker.getValue());
		String textColor = "black";
		int repeatFreq;
		try {
			repeatFreq = Integer.parseInt(repeatFreqField.getText());			
		} catch (NumberFormatException e) {
			return;
		}
		
		if (diff % repeatFreq == 0) {
			activeErrorMessages.remove(errorEndDateFrequencyMismatch);
		} else {
			textColor = "red";
			if (!activeErrorMessages.contains(errorEndDateFrequencyMismatch)) {
				activeErrorMessages.add(errorEndDateFrequencyMismatch);
			}
		}
		repeatFreqField.setStyle("-fx-text-inner-color: " + textColor + ";");
		endDatePicker.setStyle("-fx-text-inner-color: " + textColor + ";");
		
		updateErrorList();
	}
	
	// Checks all fields if any of them are empty
	private void checkEmptyFields() {
		checkIfTextFieldEmpty(purposeField, errorPurposeFieldEmpty);
		checkIfTextFieldEmpty(roomField, errorRoomFieldEmpty);
		
		if (startDatePicker.getValue() == null) {
			if (!activeErrorMessages.contains(errorStartDatePickerEmpty)) {
				activeErrorMessages.add(errorStartDatePickerEmpty);
			}
		} else {
			activeErrorMessages.remove(errorStartDatePickerEmpty);
		}

		checkIfTextFieldEmpty(startHourField, errorStartHourFieldEmpty);
		checkIfTextFieldEmpty(startMinuteField, errorStartMinuteFieldEmpty);
		checkIfTextFieldEmpty(endHourField, errorEndHourFieldEmpty);
		checkIfTextFieldEmpty(endMinuteField, errorEndMinuteFieldEmpty);
		
		if (repeatCheckbox.isSelected()) {
			checkIfTextFieldEmpty(repeatFreqField, errorRepeatFreqFieldEmpty);

			if (endDatePicker.getValue() == null) {
				if (!activeErrorMessages.contains(errorEndDatePickerEmpty)) {
					activeErrorMessages.add(errorEndDatePickerEmpty);
				}
			} else {
				activeErrorMessages.remove(errorEndDatePickerEmpty);
			}
		} else {
			activeErrorMessages.remove(errorRepeatFreqFieldEmpty);
			activeErrorMessages.remove(errorEndDatePickerEmpty);
		}
		
		updateErrorList();
	}
	
	// Checks if a text field is empty, and adds/removes error message
	private void checkIfTextFieldEmpty(TextField textField, String errorMsg) {
		if (textField.getText().equals("")) {
			if (!activeErrorMessages.contains(errorMsg)) {
				activeErrorMessages.add(errorMsg);
			}
		} else {
			activeErrorMessages.remove(errorMsg);
		}
	}
	
	// Updates the view with the current error messages
	private void updateErrorList() {
		if (activeErrorMessages.size() > 0) {
			errorPane.setVisible(true);
			String errorList = "";
			for (int i=0; i<activeErrorMessages.size(); i++) {
				errorList += activeErrorMessages.get(i) + "\n";
			}
			errorLabel.setText(errorList);			
		} else {
			errorPane.setVisible(false);
		}
		
	}
}
