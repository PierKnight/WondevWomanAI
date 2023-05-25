module com.afgc.wondevwoman {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.antlr.antlr4.runtime;


    opens com.afgc.wondevwoman to javafx.fxml;
    exports com.afgc.wondevwoman;
    exports com.afgc.wondevwoman.graphic;
    opens com.afgc.wondevwoman.graphic to javafx.fxml;
    exports com.afgc.wondevwoman.move;
    opens com.afgc.wondevwoman.move to javafx.fxml;
    exports com.afgc.wondevwoman.move.emb;
    opens com.afgc.wondevwoman.move.emb to javafx.fxml;
    exports com.afgc.wondevwoman.controller;
    opens com.afgc.wondevwoman.controller to javafx.fxml;
}