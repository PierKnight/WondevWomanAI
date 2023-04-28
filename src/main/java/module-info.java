module com.afgc.wondevwoman {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.afgc.wondevwoman to javafx.fxml;
    exports com.afgc.wondevwoman;
}