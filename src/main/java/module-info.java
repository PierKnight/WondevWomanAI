module com.afgc.wondevwoman {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.afgc.wondevwoman to javafx.fxml;
    exports com.afgc.wondevwoman;
    exports com.afgc.wondevwoman.graphic;
    opens com.afgc.wondevwoman.graphic to javafx.fxml;
    exports com.afgc.wondevwoman.players;
    opens com.afgc.wondevwoman.players to javafx.fxml;
}