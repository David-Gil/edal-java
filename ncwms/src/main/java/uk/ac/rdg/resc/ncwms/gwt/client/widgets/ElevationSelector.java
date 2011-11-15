package uk.ac.rdg.resc.ncwms.gwt.client.widgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.rdg.resc.ncwms.gwt.client.handlers.ElevationSelectionHandler;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.ListBox;

public class ElevationSelector extends BaseSelector {
	private ListBox elevations;
    private boolean positive;
    private final NumberFormat format = NumberFormat.getFormat("#0.##");
    private Map<String, String> formattedValuesToRealValues;
    
	public ElevationSelector(String title, final ElevationSelectionHandler handler) {
		super(title);
		
		elevations = new ListBox();
		elevations.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                handler.elevationSelected(getSelectedElevation());
            }
        });
		add(elevations);
		
		formattedValuesToRealValues = new HashMap<String, String>();
	}
	
	public void populateVariables(List<String> availableElevations, String currentElevation){
		elevations.clear();
		if(availableElevations == null || availableElevations.size()==0){
			label.setStylePrimaryName("inactiveLabelStyle");
			elevations.setEnabled(false);
		} else {
		    int i=0;
			for(String elevationStr : availableElevations){
			    Float elevation = Float.parseFloat(elevationStr);
			    if(!positive) elevation *= -1;
			    String formattedElevationStr = format.format(elevation); 
				elevations.addItem(formattedElevationStr);
				formattedValuesToRealValues.put(formattedElevationStr, elevationStr);
				if(elevation.equals(currentElevation)){
				    elevations.setSelectedIndex(i);
				}
				i++;
			}
			label.setStylePrimaryName("labelStyle");
			elevations.setEnabled(true);
		}
	}
	
	public void setUnitsAndDirection(String units, boolean positive){
	    this.positive = positive;
	    if(positive) {
	        label.setText("Elevation");
	        elevations.setTitle("Select the elevation");
	    } else {
	        label.setText("Depth");
	        elevations.setTitle("Select the depth");
	    }
	    if(units != null){
	        label.setText(label.getText()+" ("+units+"):");
	    }else{
	        label.setText(label.getText()+":");
	    }
	}
	
	public String getSelectedElevation(){
	    if(!elevations.isEnabled()) return null;
        return formattedValuesToRealValues.get(elevations.getValue(elevations.getSelectedIndex()));
	}

    public void setSelectedElevation(String currentElevation) {
        for(int i=0; i < elevations.getItemCount(); i++){
            String elevation = elevations.getValue(i);
            if(currentElevation.equals(elevation)){
                elevations.setSelectedIndex(i);
                return;
            }
        }
    }

    public void setEnabled(boolean enabled) {
        if(elevations.getItemCount() > 1)
            elevations.setEnabled(enabled);
        else 
            elevations.setEnabled(false);
        
        if(!elevations.isEnabled()){
            label.setStylePrimaryName("inactiveLabelStyle");
        } else {
            label.setStylePrimaryName("labelStyle");
        }
    }
}