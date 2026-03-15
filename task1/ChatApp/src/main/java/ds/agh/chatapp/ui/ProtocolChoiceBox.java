package ds.agh.chatapp.ui;

import ds.agh.chatapp.common.model.ConnectionProtocol;
import javafx.scene.control.ChoiceBox;

public class ProtocolChoiceBox extends ChoiceBox {
    public ProtocolChoiceBox() {
        super();
        this.getItems().addAll(ConnectionProtocol.valuesAsList());
        this.setValue(ConnectionProtocol.TCP);
        this.setWidth(100);
    }

    public String getSelectedProtocol() {
        return (String) this.getValue();
    }
}
