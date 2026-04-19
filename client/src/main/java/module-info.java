module grup.proje {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.client5.httpclient5.fluent;
    requires org.apache.httpcomponents.core5.httpcore5;


    opens grup.proje to javafx.fxml;
    exports grup.proje;
    exports grup.proje.UI;
    opens grup.proje.UI to javafx.fxml;
    exports grup.proje.Controller;
    opens grup.proje.Controller to javafx.fxml;
    exports grup.proje.Lobby;
    opens grup.proje.Lobby to javafx.fxml;
}